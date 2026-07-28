import { TestBed } from '@angular/core/testing';
import { TerminalPaginationComponent } from './terminal-pagination.component';

describe('TerminalPaginationComponent', () => {
  async function setup(currentPage = 0, totalPages = 5) {
    await TestBed.configureTestingModule({
      imports: [TerminalPaginationComponent],
    }).compileComponents();
    const fixture = TestBed.createComponent(TerminalPaginationComponent);
    fixture.componentRef.setInput('currentPage', currentPage);
    fixture.componentRef.setInput('totalPages', totalPages);
    fixture.detectChanges();
    await fixture.whenStable();
    return { fixture };
  }

  it('should create', async () => {
    const { fixture } = await setup();
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should show current page and total pages', async () => {
    const { fixture } = await setup(2, 10);
    expect(fixture.nativeElement.textContent).toContain('page 3 of 10');
  });

  it('should show page 1 of 1 when only one page', async () => {
    const { fixture } = await setup(0, 1);
    expect(fixture.nativeElement.textContent).toContain('page 1 of 1');
  });

  it('should emit previous page on prev click', async () => {
    const { fixture } = await setup(2, 5);
    const emitted = vi.fn();
    fixture.componentInstance.pageChange.subscribe(emitted);

    const prevBtn = fixture.nativeElement.querySelector('.page-btn');
    prevBtn.click();

    expect(emitted).toHaveBeenCalledWith(1);
  });

  it('should emit next page on next click', async () => {
    const { fixture } = await setup(1, 5);
    const emitted = vi.fn();
    fixture.componentInstance.pageChange.subscribe(emitted);

    const buttons = fixture.nativeElement.querySelectorAll('.page-btn');
    const nextBtn = buttons[1];
    nextBtn.click();

    expect(emitted).toHaveBeenCalledWith(2);
  });

  it('should disable prev button on first page', async () => {
    const { fixture } = await setup(0, 5);
    const prevBtn = fixture.nativeElement.querySelector('.page-btn');
    expect(prevBtn.disabled).toBe(true);
  });

  it('should disable next button on last page', async () => {
    const { fixture } = await setup(4, 5);
    const buttons = fixture.nativeElement.querySelectorAll('.page-btn');
    const nextBtn = buttons[1];
    expect(nextBtn.disabled).toBe(true);
  });

  it('should not emit prev when on first page', async () => {
    const { fixture } = await setup(0, 3);
    const emitted = vi.fn();
    fixture.componentInstance.pageChange.subscribe(emitted);

    const prevBtn = fixture.nativeElement.querySelector('.page-btn');
    prevBtn.click();

    expect(emitted).not.toHaveBeenCalled();
  });

  it('should not emit next when on last page', async () => {
    const { fixture } = await setup(2, 3);
    const emitted = vi.fn();
    fixture.componentInstance.pageChange.subscribe(emitted);

    const buttons = fixture.nativeElement.querySelectorAll('.page-btn');
    const nextBtn = buttons[1];
    nextBtn.click();

    expect(emitted).not.toHaveBeenCalled();
  });
});
