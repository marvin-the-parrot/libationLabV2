import {Component} from '@angular/core';
import {GroupOverview} from "../../../dtos/group-overview";
import {GroupsService} from "../../../services/groups.service";
import {ActivatedRoute} from "@angular/router";
import {Observable, Subject, of} from "rxjs";
import {UserListDto, UserListGroupDto} from "../../../dtos/user";
import {UserService} from "../../../services/user.service";
import {MessageCreate} from "../../../dtos/message";
import {MessageService} from "../../../services/message.service";
import { ToastrService } from 'ngx-toastr';
import {Router} from "@angular/router";
import { DialogService } from 'src/app/services/dialog.service';

@Component({
  selector: 'app-group-detail',
  templateUrl: './group-detail.component.html',
  styleUrls: ['./group-detail.component.scss']
})
export class GroupDetailComponent {

  group: GroupOverview = {
    id: 1,
    name: 'Cocktail Party',
    cocktails: ['Mochito', 'Mai Tai', 'White Russian'],
    members:[{name: 'Sep', id: 4, isHost: true}, {name: 'Jan', id: 5, isHost: false}],
  }

  user: UserListDto = {
    id: null,
    name: ''
  };

  dummyMemberSelectionModel: unknown; // Just needed for the autocomplete
  submitted = false;
  // Error flag
  error = false;
  errorMessage = '';
  private destroy$ = new Subject<void>();

  constructor(
    private groupsService: GroupsService,
    private userService: UserService,
    private service: GroupsService,
    private dialogService: DialogService,
    private messageService: MessageService,
    private notification: ToastrService,
    private route: ActivatedRoute,
    private router: Router,
  ) {
  }

  ngOnInit(): void {
    const groupId = this.route.snapshot.params['id'];
    this.groupsService.getById(groupId).subscribe(
      (group: GroupOverview) => {
        this.group = group;
      },
      error => {
        console.error('Error fetching group details for editing', error);
        // Handle error appropriately (e.g., show a message to the user)
      }
    );
    this.groupsService.getMembersOfGroup(groupId).subscribe(
      (members: UserListGroupDto[]) => { 
        this.group.members = members;
      },
      error => {
        console.error('Error fetching group details for editing', error);
        this.notification.error(`Error in searching memebers of "${this.group.name}".`);
      }
    );
  }

  memberSuggestions = (input: string): Observable<UserListDto[]> => (input === '')
    ? of([])
    : this.userService.search(input);

  public formatMember(member: UserListDto | null): string {
    return member?.name ?? '';
  }

  /**
   * Create Message
   */
  createMessage(userId: number) {
    const groupId = this.route.snapshot.params['id'];
    this.submitted = true;
    const createMessage: MessageCreate = new MessageCreate(userId, groupId);
    this.create(createMessage);
  }

  public onDelete(memberId: number): void {
    this.dialogService.openOptionDialog().subscribe((option) => {
      if (option) {
        this.dialogService.openDeleteConfirmation().subscribe((result) => {
          if (result) {
            const observable = this.service.deleteByMemberByIdAndGroupId(this.group.id, memberId , 1/** PLACE HOLDER**/); //TODO - logged used id
            observable.subscribe({
              next: data => {
                this.notification.success(`Successfully deleted Group "${this.group.name}".`);
                this.router.navigate(['/groups']);
              },
              error: error => {
                console.error('Error deleting group', error);
                this.notification.error(`Error deleting group "${this.group.name}".`);
              }
            }); 
          }
        });
      }
    });
  }

  /**
   * Send Creat data to the backend if it was successfully, the user will be forwarded
   *
   * @param messageCreate create account data from the form
   */
  create(messageCreate: MessageCreate) {
    this.messageService.createMessage(messageCreate).subscribe({
      next: () => {

      },
      error: error => {
        console.log('Could not create account due to:');
        console.log(error);
        this.error = true;
        if (typeof error.error === 'object') {
          this.errorMessage = error.error.error;
        } else {
          this.errorMessage = error.error;
        }
      }
    });
  }
}
function takeUntil(destroy$: any): any {
  throw new Error('Function not implemented.');
}

