import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { Session } from '../interfaces/session.interface';

import { SessionApiService } from './session-api.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";

describe('SessionsService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SessionApiService]
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });


  it('should GET all sessions', () => {
    const mockSessions: Session[] = [
      { id: 1, name: 'Session A' } as any,
      { id: 2, name: 'Session B' } as any
    ];

    service.all().subscribe((sessions) => {
      expect(sessions).toEqual(mockSessions);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/session');
    expect(req.request.method).toBe('GET');
    req.flush(mockSessions);
  });

  it('should DELETE session by ID', () => {
    service.delete('123').subscribe((res) => {
      expect(res).toEqual({});
    });

    const req = httpMock.expectOne('http://localhost:8080/api/session/123');
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

  it('should POST new session', () => {
    const newSession: Session = { name: 'New Session' } as any;
    const mockResponse: Session = { id: 1, name: 'New Session' } as any;

    service.create(newSession).subscribe((response) => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newSession);
    req.flush(mockResponse);
  });

  it('should PUT (update) existing session', () => {
    const updatedSession: Session = { id: 1, name: 'Updated Session' } as any;

    service.update('1', updatedSession).subscribe((response) => {
      expect(response).toEqual(updatedSession);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/session/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedSession);
    req.flush(updatedSession);
  });

  it('should POST to participate endpoint', () => {
    service.participate('1', '42').subscribe((response) => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/session/1/participate/42');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();
    req.flush(null);
  });

  it('should DELETE from unParticipate endpoint', () => {
    service.unParticipate('1', '42').subscribe((response) => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/session/1/participate/42');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

});
