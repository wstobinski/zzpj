import {ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {PlayersPage} from './players.page';
import {PlayersService} from "../../services/players.service";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {DebugElement} from "@angular/core";
import {Player} from "../../model/player.model";
import {environment} from "../../../environments/environment";
import {By} from "@angular/platform-browser";
import {AuthService} from "../../services/auth.service";
import {ApiService} from "../../services/api.service";
import {Utils} from "../../utils/utils";
import {MockStorage} from "../../services/tests/mock-storage.service";
import {MockUtils} from "../../services/tests/mock-utils.service";
import {AngularDelegate, IonicModule, ModalController, PopoverController} from "@ionic/angular";
import {HandballComponentsModule} from "../../handball-components.module";
import {of, Subject} from "rxjs";
import {UserService} from "../../services/user.service";
import {UserAuthData} from "../../model/UserAuthData";
import {MockApiService} from "../../services/tests/mock-api-service";

describe('PlayersPage', () => {
  let component: PlayersPage;
  let fixture: ComponentFixture<PlayersPage>;
  let playersService: PlayersService;
  let httpMock: HttpTestingController;
  let debugElement: DebugElement;

  beforeEach(async () => {
    const user = { role: 'admin' }; // Mock user object
    const userAuthData = { token: 'your-token' }; // Mock userAuthData object
    // Create a spy object for UserService
    const userServiceSpy = jasmine.createSpyObj('UserService', ['getUser']);
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['userAuthData']);
    // Configure the getUser spy to return the mock user object
    userServiceSpy.getUser.and.returnValue(of(user));
    authServiceSpy.userAuthData = new Subject<UserAuthData>();

    await TestBed.configureTestingModule({
      declarations: [PlayersPage],
      imports: [HttpClientTestingModule,
        IonicModule.forRoot(),
        HandballComponentsModule],
      providers: [
        PlayersService,
        { provide: Storage, useClass: MockStorage },
        { provide: AuthService, useValue: authServiceSpy},
        { provide: ApiService, useClass: MockApiService},
        { provide: Utils, useClass: MockUtils },
        { provide: UserService, useValue: userServiceSpy }, // Provide the mocked UserService
        ModalController,
        AngularDelegate,
        PopoverController
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayersPage);
    component = fixture.componentInstance;
    playersService = TestBed.inject(PlayersService);
    httpMock = TestBed.inject(HttpTestingController);
    debugElement = fixture.debugElement;
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch and display players', fakeAsync(() => {
    spyOn(console, 'log');
    const mockPlayers: Player[] = [
      { uuid: 1, firstName: 'Player', lastName: "One", pitchNumber: 1, suspended: false, captain: false},
      { uuid: 2, firstName: 'Player', lastName: "Two", pitchNumber: 2, suspended: false, captain: false},
    ];

    // Trigger component initialization
    fixture.detectChanges();

    // Expect an HTTP request to the specified URL
    const req = httpMock.expectOne(`${environment.API_URL}/players`);
    expect(req.request.method).toBe('GET');

    // Respond to the HTTP request with mock data
    req.flush({ response: mockPlayers });

    // Advance the virtual clock to simulate completion of asynchronous operations
    tick();

    // Trigger change detection to update the view with new data
    fixture.detectChanges();

    // Verify that players are displayed in the view
    const playerElements = debugElement.queryAll(By.css('.data-row'));
    expect(playerElements.length).toBe(2);
    expect(playerElements[0].nativeElement.textContent).toContain('Player One');
    expect(playerElements[1].nativeElement.textContent).toContain('Player Two');
  }));
});
