import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';

export const columns: BasicColumn[] = [
    {
    title: '游戏选手id',
    dataIndex: 'chessPlayerId'
   },
   {
    title: '游戏id',
    dataIndex: 'chessGameId'
   },
   {
    title: '得分，>0为获取，<0为失去',
    dataIndex: 'score'
   },
   {
    title: '所属部门',
    dataIndex: 'sysOrgCode'
   },
   {
    title: '删除状态',
    dataIndex: 'delFlag'
   },
];

export const searchFormSchema: FormSchema[] = [
 {
    label: '游戏选手id',
    field: 'chessPlayerId',
    component: 'Input'
  },
 {
    label: '游戏id',
    field: 'chessGameId',
    component: 'Input'
  },
];

export const formSchema: FormSchema[] = [
  // TODO 主键隐藏字段，目前写死为ID
  {label: '', field: 'id', component: 'Input', show: false},
  {
    label: '游戏选手id',
    field: 'chessPlayerId',
    component: 'Input',
  },
  {
    label: '游戏id',
    field: 'chessGameId',
    component: 'Input',
  },
  {
    label: '得分，>0为获取，<0为失去',
    field: 'score',
    component: 'InputNumber',
  },
  {
    label: '所属部门',
    field: 'sysOrgCode',
    component: 'Input',
  },
  {
    label: '删除状态',
    field: 'delFlag',
    component: 'InputNumber',
  },
];
