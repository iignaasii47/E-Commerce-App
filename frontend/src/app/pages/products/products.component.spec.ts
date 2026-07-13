import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ProductsComponent } from './products.component';

describe('ProductsComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductsComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(ProductsComponent);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should render product grid', () => {
    const fixture = TestBed.createComponent(ProductsComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.product-grid')).toBeTruthy();
  });

  it('should render all 10 products by default', () => {
    const fixture = TestBed.createComponent(ProductsComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    const cards = compiled.querySelectorAll('.t-card');
    expect(cards.length).toBe(10);
  });
});
