import {ComponentFixture, TestBed} from '@angular/core/testing';
import {RefereesPage} from './referees.page';
import {of, Subject} from "rxjs";
import {UserAuthData} from "../../model/UserAuthData";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {AngularDelegate, IonicModule, ModalController, PopoverController} from "@ionic/angular";
import {HandballComponentsModule} from "../../handball-components.module";
import {PlayersService} from "../../services/players.service";
import {MockStorage} from "../../services/tests/mock-storage.service";
import {AuthService} from "../../services/auth.service";
import {ApiService} from "../../services/api.service";
import {MockApiService} from "../../services/tests/mock-api-service";
import {Utils} from "../../utils/utils";
import {MockUtils} from "../../services/tests/mock-utils.service";
import {UserService} from "../../services/user.service";
import {RefereeService} from "../../services/referee.service";

describe('RefereesPage', () => {
  let component: RefereesPage;
  let fixture: ComponentFixture<RefereesPage>;

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
      declarations: [RefereesPage],
      imports: [HttpClientTestingModule,
        IonicModule.forRoot(),
        HandballComponentsModule],
      providers: [
        RefereeService,
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
    fixture = TestBed.createComponent(RefereesPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
