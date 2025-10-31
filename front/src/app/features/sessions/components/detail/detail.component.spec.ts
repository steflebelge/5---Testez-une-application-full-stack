import {HttpClientModule} from '@angular/common/http';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {RouterTestingModule,} from '@angular/router/testing';
import {expect} from '@jest/globals';
import {Router, ActivatedRoute} from '@angular/router';
import {SessionService} from '../../../../services/session.service';
import {TeacherService} from '../../../../services/teacher.service';
import {DetailComponent} from './detail.component';
import {of} from "rxjs";
import { FormBuilder } from '@angular/forms';



describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let mockRoute: any;
  let mockRouter: jest.Mocked<Router>;
  let mockSessionService: any;
  let mockTeacherService: jest.Mocked<TeacherService>;
  let mockSnackBar: jest.Mocked<MatSnackBar>;

  beforeEach(async () => {
    mockRoute = {
      snapshot: {paramMap: new Map([['id', '123']])}
    };

    mockRouter = {navigate: jest.fn()} as any;
    mockSessionService = {
      sessionInformation: {id: 1, admin: true}
    };
    mockSnackBar = {open: jest.fn()} as any;

    mockTeacherService = {
      detail: jest.fn()
    } as any;

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent],
      providers: [
        FormBuilder,
        {provide: ActivatedRoute, useValue: mockRoute},
        {provide: Router, useValue: mockRouter},
        {provide: SessionService, useValue: mockSessionService},
        {provide: TeacherService, useValue: mockTeacherService},
        {provide: MatSnackBar, useValue: mockSnackBar}
      ]
    })
      .compileComponents();
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.sessionId).toBe('123');
    expect(component.userId).toBe('1');
    expect(component.isAdmin).toBe(true);
  });

  it('should call window.history.back() when back() is called', () => {
    const spyBack = jest.spyOn(window.history, 'back').mockImplementation(() => {});
    component.back();
    expect(spyBack).toHaveBeenCalled();
  });
});

