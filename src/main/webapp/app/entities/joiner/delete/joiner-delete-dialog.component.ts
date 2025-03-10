import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IJoiner } from '../joiner.model';
import { JoinerService } from '../service/joiner.service';

@Component({
  templateUrl: './joiner-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class JoinerDeleteDialogComponent {
  joiner?: IJoiner;

  protected joinerService = inject(JoinerService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.joinerService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
