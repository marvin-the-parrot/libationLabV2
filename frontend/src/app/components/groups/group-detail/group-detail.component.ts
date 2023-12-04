import {Component} from '@angular/core';
import {GroupOverview} from "../../../dtos/group-overview";
import {GroupsService} from "../../../services/groups.service";
import {ActivatedRoute} from "@angular/router";
import {Observable, Subject, of} from "rxjs";
import {UserListDto} from "../../../dtos/user";
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
    cocktails: ['Mojito', 'Mai Tai', 'White Russian'],
    members: [{name: 'Sep', id: 4, isHost:false}, {name: 'Jan', id: 5,isHost:false}, {name: 'Peter', id: 6,isHost:false}, {name: 'Susanne', id: 7,isHost:false}],
  }

  username: string = localStorage.getItem('username');

  // for autocomplete
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

  }

  memberSuggestions = (input: string): Observable<UserListDto[]> => (input === '')
    ? of([])
    : this.userService.search(input, this.route.snapshot.params['id']);

  public formatMember(member: UserListDto | null): string {
    return member?.name ?? '';
  }

  /**
   * Create Message
   */
  createMessage(userId: number) {
    if (userId == null) {
      return;
    }
    const groupId = this.route.snapshot.params['id'];
    this.submitted = true;
    const createMessage: MessageCreate = new MessageCreate(userId, groupId);
    this.create(createMessage);
  }

  public openMemberOptions(member: UserListDto): void {
    this.dialogService.openOptionDialog().subscribe((deleteOption) => {
      if (deleteOption) {
        this.removeMemberFromGroup(member);
      } else {
        console.log("Make host");
      }
    });
  }

  private removeMemberFromGroup(member: UserListDto) {
    this.dialogService.openDeleteConfirmation().subscribe((result) => {
      if (result) {
        this.service.removeMemberFromGroup(this.group.id, member.id).subscribe({
          next: data => {
            this.notification.success(`Successfully removed '${member.name}' from Group '${this.group.name}'.`);
          },
          error: error => {
            console.error(`Error removing member '${member.name}' from group.`, error);
            this.notification.error(`Error removing member '${member.name}' from group.`); // todo: show error message from backend
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
        this.notification.success('Successfully invited ' + this.user.name + ' to ' + this.group.name + '!');
      },
      error: error => {
        console.log('Could not create message due to:');
        this.defaultServiceErrorHandling(error);
      }
    });
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    this.notification.error(error.error.detail);
  }
}
function takeUntil(destroy$: any): any {
  throw new Error('Function not implemented.');
}


