import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

import { SessionService } from './session.service';

describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SessionService]
    });
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should set sessionInformation and emit true on logIn()', (done) => {
    const fakeUser: SessionInformation = { id: 1, token: 'abc', admin: false } as any;

    let emitted: boolean[] = [];
    service.$isLogged().subscribe((value) => emitted.push(value));

    service.logIn(fakeUser);

    expect(service.sessionInformation).toEqual(fakeUser);
    expect(service.isLogged).toBe(true);
    expect(emitted.at(-1)).toBe(true);
    done();
  });

  it('should clear sessionInformation and emit false on logOut()', (done) => {
    const fakeUser: SessionInformation = { id: 1, token: 'abc', admin: true } as any;
    service.logIn(fakeUser);

    let emitted: boolean[] = [];
    service.$isLogged().subscribe((value) => emitted.push(value));

    service.logOut();

    expect(service.sessionInformation).toBeUndefined();
    expect(service.isLogged).toBe(false);
    expect(emitted.at(-1)).toBe(false);
    done();
  });

});
