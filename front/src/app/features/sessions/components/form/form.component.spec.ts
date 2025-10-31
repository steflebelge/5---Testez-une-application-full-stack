import {HttpClientModule} from '@angular/common/http';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RouterTestingModule} from '@angular/router/testing';
import {expect} from '@jest/globals';
import {SessionService} from 'src/app/services/session.service';
import {ActivatedRoute, Router} from '@angular/router';
import {TeacherService} from '../../../../services/teacher.service';
import {SessionApiService} from '../../services/session-api.service';

import {FormComponent} from './form.component';
import {of} from "rxjs";

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  let mockRouter: jest.Mocked<Router>;
  let mockActivatedRoute: any;
  let mockMatSnackBar: jest.Mocked<MatSnackBar>;
  let mockSessionApiService: jest.Mocked<SessionApiService>;
  let mockSessionService: any;
  let mockTeacherService: jest.Mocked<TeacherService>;

  beforeEach(async () => {
    mockRouter = { navigate: jest.fn(), url: '/sessions/create' } as any;
    mockActivatedRoute = { snapshot: { paramMap: new Map([['id', '123']]) } };
    mockMatSnackBar = { open: jest.fn() } as any;
    mockSessionApiService = {
      create: jest.fn(),
      update: jest.fn(),
      detail: jest.fn()
    } as any;
    mockSessionService = {
      sessionInformation: { admin: true }
    };
    mockTeacherService = {
      all: jest.fn().mockReturnValue(of([]))
    } as any;


    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        FormBuilder,
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: SessionService, useValue: mockSessionService },
        { provide: TeacherService, useValue: mockTeacherService }
      ],
      declarations: [FormComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect non-admin users on init', () => {
    mockSessionService.sessionInformation.admin = false;
    const spyNavigate = jest.spyOn(mockRouter, 'navigate');
    component.ngOnInit();
    expect(spyNavigate).toHaveBeenCalledWith(['/sessions']);
    mockSessionService.sessionInformation.admin = true; // reset
  });

  it('should set onUpdate true and call initForm with session when update route', () => {
    (component as any).router = {...mockRouter, url: '/sessions/update/123'} as any;

    const fakeSession = {
      id: 123,
      name: 'Test session',
      date: new Date().toISOString(),
      teacher_id: 2,
      description: 'desc'
    } as any;

    mockSessionApiService.detail.mockReturnValue(of(fakeSession));
    const spyInit = jest.spyOn<any, any>(component as any, 'initForm');

    component.ngOnInit();

    expect(component.onUpdate).toBe(true);
    expect(mockSessionApiService.detail).toHaveBeenCalledWith('123');
    expect(spyInit).toHaveBeenCalledWith(fakeSession);
  });

  it('should call create() and exitPage on submit (create mode)', () => {
    const spyExit = jest.spyOn<any, any>(component as any, 'exitPage');
    mockSessionApiService.create.mockReturnValue(of({} as any));

    component.onUpdate = false;
    component.sessionForm = new FormBuilder().group({
      name: ['Session A'],
      date: ['2025-01-01'],
      teacher_id: [1],
      description: ['desc']
    });

    component.submit();

    expect(mockSessionApiService.create).toHaveBeenCalled();
    expect(spyExit).toHaveBeenCalledWith('Session created !');
  });

  it('should call update() and exitPage on submit (update mode)', () => {
    const spyExit = jest.spyOn<any, any>(component as any, 'exitPage');
    mockSessionApiService.update.mockReturnValue(of({} as any));

    component.onUpdate = true;
    (component as any).id = '456';
    component.sessionForm = new FormBuilder().group({
      name: ['Session B'],
      date: ['2025-02-01'],
      teacher_id: [2],
      description: ['desc2']
    });

    component.submit();

    expect(mockSessionApiService.update).toHaveBeenCalledWith('456', expect.any(Object));
    expect(spyExit).toHaveBeenCalledWith('Session updated !');
  });

});
