import { Input, Component, OnInit, EventEmitter, Output, Inject, OnChanges, SimpleChanges } from '@angular/core';
import { TreeService } from './tree.service';
import { TreeModel, Ng2TreeSettings, Ng2TreeOptions } from './tree.types';
import { NodeEvent } from './tree.events';
import { Tree } from './tree';

@Component({
  selector: 'tree',
  template: `<tree-internal [tree]="tree" [options]="options"></tree-internal>`,
  providers: [TreeService]
})
export class TreeComponent implements OnInit, OnChanges {
  private static EMPTY_TREE: Tree = new Tree({id: undefined, value: '', type: 0});

  /* tslint:disable:no-input-rename */
  @Input('tree')
  public treeModel: TreeModel;
  /* tslint:enable:no-input-rename */

  @Input()
  public settings: Ng2TreeSettings;

    @Input()
    public options: Ng2TreeOptions;

  // @Output()
  // public nodeCreated: EventEmitter<any> = new EventEmitter();
  @Output()
  public nodeCreatedRemote: EventEmitter<any> = new EventEmitter();

  // @Output()
  // public nodeRemoved: EventEmitter<any> = new EventEmitter();
    @Output()
    public nodeRemovedRemote: EventEmitter<any> = new EventEmitter();

  // @Output()
  // public nodeRenamed: EventEmitter<any> = new EventEmitter();
  @Output()
  public nodeRenamedRemote: EventEmitter<any> = new EventEmitter();

  // @Output()
  // public nodeMoved: EventEmitter<any> = new EventEmitter();
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
      let that = this;

    // this.treeService.nodeRemoved$.subscribe((e: NodeEvent) => {
    //   this.nodeRemoved.emit(e);
    // });
      this.treeService.nodeRemovedRemote$.subscribe((e: NodeEvent) => {
          this.nodeRemovedRemote.emit(e);
      });

    // this.treeService.nodeRenamed$.subscribe((e: NodeEvent) => {
    //   this.nodeRenamed.emit(e);
    // });
    this.treeService.nodeRenamedRemote$.subscribe((e: NodeEvent) => {
      this.nodeRenamedRemote.emit(e);
    });

    // this.treeService.nodeCreated$.subscribe((e: NodeEvent) => {
    //   this.nodeCreated.emit(e);
    // });
    this.treeService.nodeCreatedRemote$.subscribe((e: NodeEvent) => {
      this.nodeCreatedRemote.emit(e);
    });

    // this.treeService.nodeMoved$.subscribe((e: NodeEvent) => {
    //   this.nodeMoved.emit(e);
    // });
    this.treeService.nodeMovedRemote$.subscribe((e: NodeEvent) => {
      this.nodeMovedRemote.emit(e);
    });

    this.treeService.nodeSelected$.subscribe((e: NodeEvent) => {
      this.nodeSelected.emit(e);
    });

  }
}
