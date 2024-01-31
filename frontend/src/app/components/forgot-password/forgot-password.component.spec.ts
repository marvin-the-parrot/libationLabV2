import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';
import {ForgotPasswordComponent} from './forgot-password.component';
import {ReactiveFormsModule} from '@angular/forms';
import {RouterTestingModule} from '@angular/router/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';


describe('ForgotPasswordComponent', () => {
    let component: any;
    let fixture: ComponentFixture<ForgotPasswordComponent>;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule, RouterTestingModule, ReactiveFormsModule],
            declarations: [ForgotPasswordComponent]
        })
            .compileComponents();
    }));
    beforeEach(() => {
        fixture = TestBed.createComponent(ForgotPasswordComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });
    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
