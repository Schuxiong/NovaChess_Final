import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';

export const columns: BasicColumn[] = [
    {
    title: '用户id',
    dataIndex: 'userId'
   },
   {
    title: '用户账号',
    dataIndex: 'userAccount'
   },
   {
    title: '得分，初始值600',
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
    label: '用户id',
    field: 'userId',
    component: 'Input'
  },
 {
    label: '用户账号',
    field: 'userAccount',
    component: 'Input'
  },
];

export const formSchema: FormSchema[] = [
  // TODO 主键隐藏字段，目前写死为ID
  {label: '', field: 'id', component: 'Input', show: false},
  {
    label: '用户id',
    field: 'userId',
    component: 'Input',
  },
  {
    label: '用户账号',
    field: 'userAccount',
    component: 'Input',
  },
  {
    label: '得分，初始值600',
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
