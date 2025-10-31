import {ComponentFixture, TestBed} from '@angular/core/testing';
import { Router } from '@angular/router';
import { SessionService } from '../../services/session.service';
import { UserService } from '../../services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';

import {MeComponent} from './me.component';
import {User} from "../../interfaces/user.interface";
import {of} from "rxjs";

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  let mockRouter: jest.Mocked<Router>;
  let mockUserService: jest.Mocked<UserService>;
  let mockSnackBar: jest.Mocked<MatSnackBar>;
  let mockSessionService: any;

  beforeEach(async () => {
    mockRouter = {navigate: jest.fn()} as any;
    mockUserService = {
      getById: jest.fn(),
      delete: jest.fn()
    } as any;
    mockSnackBar = {
      open: jest.fn()
    } as any;
    mockSessionService = {
      sessionInformation: {id: 1, admin: true},
      logOut: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
      ],
      providers: [
        {provide: SessionService, useValue: mockSessionService},
        {provide: UserService, useValue: mockUserService},
        {provide: MatSnackBar, useValue: mockSnackBar},
        {provide: Router, useValue: mockRouter},
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call userService.getById and assign user', () => {
    const fakeUser: User = { id: 1, name: 'John Doe' } as any;
    mockUserService.getById.mockReturnValue(of(fakeUser));

    component.ngOnInit();

    expect(mockUserService.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual(fakeUser);
  });

  it('should call window.history.back() when back() is called', () => {
    const spyBack = jest.spyOn(window.history, 'back').mockImplementation(() => {
    });
    component.back();
    expect(spyBack).toHaveBeenCalled();
  });


  it('should delete user, show snackbar, log out and navigate to root', () => {
    mockUserService.delete.mockReturnValue(of({}));

    component.delete();

    expect(mockUserService.delete).toHaveBeenCalledWith('1');
    expect(mockSnackBar.open)
      .toHaveBeenCalledWith('Your account has been deleted !', 'Close', { duration: 3000 });
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });

});
