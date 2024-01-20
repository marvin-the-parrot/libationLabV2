import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {CocktailFeedbackDto} from "../dtos/cocktail";
import {MenuCocktailsDto} from "../dtos/menu";
import {FeedbackCreateDto} from "../dtos/feedback";

@Injectable({
    providedIn: 'root'
})
export class FeedbackService {

    private feedbackBaseUri: string = this.globals.backendUri + '/feedback';

    constructor(
        private httpClient: HttpClient,
        private globals: Globals
    ) {
    }

    updateCocktailFeedback(feedbackToUpdate: CocktailFeedbackDto) {
        return this.httpClient.put(this.feedbackBaseUri + `/update`, {feedbackToUpdate});
    }

    createFeedbackRelations(feedbackRelations: FeedbackCreateDto)  {
        return this.httpClient.post(this.feedbackBaseUri + '/create', feedbackRelations);
    }



}
