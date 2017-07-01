import { Input, Component, OnInit, EventEmitter, Output, Inject, OnChanges, SimpleChanges } from '@angular/core';
import { TreeService } from './tree.service';

import { TreeModel } from './type/tree.model';
import { TreeSettings } from './type/tree.settings';
import { TreeOptions } from './type/tree.options';

import { NodeEvent, NodeDraggingEvent } from './tree.events';
import { Tree } from './tree';

import { CONSTANT } from '../../../utils/constant';
import { Utils } from '../../../utils/utils';

@Component({
  selector: 'tree',
  template: `
    <div class="tree-toolbar-container">
      <tree-toolbar [tree]="tree" [options]="options"></tree-toolbar>
    </div>
     <div class="tree-internal-container y-scrollable">
      <tree-internal [tree]="tree" [options]="options" [class.filter]="options.keywords"></tree-internal>
    </div>
   `,
  styleUrls: ['./styles.scss'],
  providers: [TreeService]
})
export class TreeComponent implements OnInit, OnChanges {
  private static EMPTY_TREE: Tree = new Tree({id: undefined, value: '', type: 'root'});

  @Input('tree')
  public treeModel: TreeModel;


  @Input()
  public settings: TreeSettings;

    @Input()
    public options: TreeOptions;

  @Output()
  public nodeCreatedRemote: EventEmitter<any> = new EventEmitter();

    @Output()
    public nodeRemovedRemote: EventEmitter<any> = new EventEmitter();

  @Output()
  public nodeRenamedRemote: EventEmitter<any> = new EventEmitter();

  @Output()
  public nodeMovedRemote: EventEmitter<any> = new EventEmitter();

  @Output()
  public nodeSelected: EventEmitter<any> = new EventEmitter();

  public tree: Tree;

  public constructor(@Inject(TreeService) private treeService: TreeService) {

  }

  public ngOnChanges(changes: SimpleChanges): void {
    if (!this.treeModel) {
      this.tree = TreeComponent.EMPTY_TREE;
    } else {
      this.tree = Tree.buildTreeFromModel(this.treeModel);
    }
  }

  public ngOnInit(): void {

    this.treeService.nodeDraging$.subscribe((e: NodeDraggingEvent) => {
      if (e.act === 'start') {
        this.tree.isDragging = true;
      } else {
        this.tree.isDragging = false;
      }
    });

    this.treeService.nodeRemovedRemote$.subscribe((e: NodeEvent) => {
        this.nodeRemovedRemote.emit(e);
    });

    this.treeService.nodeRenamedRemote$.subscribe((e: NodeEvent) => {
      this.nodeRenamedRemote.emit(e);
    });

    this.treeService.nodeCreatedRemote$.subscribe((e: NodeEvent) => {
      this.nodeCreatedRemote.emit(e);
    });

    this.treeService.nodeMovedRemote$.subscribe((e: NodeEvent) => {
      this.nodeMovedRemote.emit(e);
    });

    this.treeService.nodeSelected$.subscribe((e: NodeEvent) => {
      this.nodeSelected.emit(e);
    });

  }
}
