import {Component, OnInit} from '@angular/core';
import {GroupOverview} from "../../dtos/group-overview";
import {GroupsService} from "../../services/groups.service";
import {MenuCocktailsDto} from 'src/app/dtos/menu';
import {CocktailService} from 'src/app/services/cocktail.service';
import {Observable, of} from 'rxjs';
import {map, catchError} from 'rxjs/operators';
import {ToastrService} from "ngx-toastr";


@Component({
  selector: 'app-groups',
  templateUrl: './groups.component.html',
  styleUrls: ['./groups.component.scss']
})
export class GroupsComponent implements OnInit {

  groups: GroupOverview[] = null;

  username: string = JSON.parse(localStorage.getItem('user')).name;

  constructor(
    public groupService: GroupsService,
    private cocktailService: CocktailService,
    private notification: ToastrService,
  ) {
  }

  ngOnInit() {
    this.fetchGroups();
  }

  fetchGroups() {
    this.groupService.getAllByUser().subscribe({
      next: (groups: GroupOverview[]) => {
        for (let group of groups) {
          if (group.cocktails == null) {
            this.getCocktailsMenu(group.id).subscribe({
              next: (cocktails: string[]) => {
                group.cocktails = cocktails;
              },
              error: error => {
                console.error(`Error fetching cocktails for group ${group.id}`, error);
                this.notification.error(error.error, `Error fetching cocktails for group ${group.name}.`, {
                  enableHtml: true,
                  timeOut: 10000,
                });
              }
            });
          }
        }
        this.groups = groups;
      },
      error: error => {
        console.error('Error fetching groups', error);
        this.notification.error(`Error fetching groups.`);
      }
    });
  }

  private getCocktailsMenu(groupId: number): Observable<string[]> {
    return this.cocktailService.getCocktailMenu(groupId).pipe(
      map((menu: MenuCocktailsDto) => menu.cocktailsList.map(cocktail => cocktail.name)),
      catchError(error => {
        console.error(`Error fetching cocktail menu for group ${groupId}`, error);
        this.notification.error(error.error, `Error fetching cocktail menu for group ${groupId}.`, {
          enableHtml: true,
          timeOut: 10000,
        });
        return of([]);
      })
    );
  }

}
