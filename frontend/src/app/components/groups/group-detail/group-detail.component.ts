import {Component} from '@angular/core';
import {GroupOverview} from "../../../dtos/group-overview";
import {GroupsService} from "../../../services/groups.service";
import {ActivatedRoute} from "@angular/router";
import {Observable, of} from "rxjs";
import {UserListDto} from "../../../dtos/user";
import {UserService} from "../../../services/user.service";
import {MessageCreate} from "../../../dtos/message";
import {MessageService} from "../../../services/message.service";
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-group-detail',
  templateUrl: './group-detail.component.html',
  styleUrls: ['./group-detail.component.scss']
})
export class GroupDetailComponent {

  group: GroupOverview = {
    id: 1,
    name: 'Cocktail Party',
    host: {name: 'Mr X', id: 1},
    cocktails: ['Mochito', 'Mai Tai', 'White Russian'],
    members:[],
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

  constructor(
    private groupsService: GroupsService,
    private userService: UserService,
    private messageService: MessageService,
    private notification: ToastrService,
    private route: ActivatedRoute
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
      (members: UserListDto[]) => {
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
