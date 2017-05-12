import {Component, ViewEncapsulation, Input, Output, EventEmitter} from '@angular/core';

@Component({
  selector: 'slidebar-item',
  encapsulation: ViewEncapsulation.None,
  styleUrls: [],
  templateUrl: './slidebar-item.html'
})
export class SlidebarItem {

  @Input() menuItem:any;
  @Input() selected:boolean = false;
  @Input() child:boolean = false;

  @Output() itemHover = new EventEmitter<any>();
  @Output() itemSelect = new EventEmitter<any>();
  @Output() toggleSubMenu = new EventEmitter<any>();

  public onHoverItem($event):void {
    this.itemHover.emit($event);
  }
  public onSelectItem($event, item):void {
    this.itemSelect.emit({ event: $event, item: item });
  }

}
