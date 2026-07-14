import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { App } from './app';
import { environment } from '../environments/environment';

describe('App', () => {
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  function createFixture() {
    const fixture = TestBed.createComponent(App);
    httpMock.expectOne(environment.apiUrl + '/api/cart').flush([]);
    return fixture;
  }

  it('should create the app', () => {
    const fixture = createFixture();
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render the terminal window', () => {
    const fixture = createFixture();
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.terminal-window')).toBeTruthy();
  });

  it('should render the titlebar', () => {
    const fixture = createFixture();
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.titlebar')).toBeTruthy();
  });

  it('should render the statusbar', () => {
    const fixture = createFixture();
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.statusbar')).toBeTruthy();
  });
});
