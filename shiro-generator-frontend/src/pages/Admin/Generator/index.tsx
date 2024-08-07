import { type ActionType, ProColumns, ProTable } from "@ant-design/pro-components";
import { deleteGeneratorUsingPost, listGeneratorByPageUsingPost } from "@/services/backend/generatorController";
import React, { useRef, useState } from "react";
import { Button, Input, message, Select, Space, Tag, Typography, Image } from "antd";
import { FormInstance } from "antd/lib";
import { useEmotionCss } from "@ant-design/use-emotion-css";
import { PlusOutlined } from "@ant-design/icons";
import { MINIO_HOST } from "@/constants";
import { Link } from "@umijs/max";

const Generator = () => {

  const [tags, setTags] = useState<string[]>([]);
  const formRef = useRef<FormInstance>();
  const actionRef = useRef<ActionType>();

  /**
   * 删除节点
   *
   * @param row
   */
  const handleDelete = async (row: API.Generator) => {
    const hide = message.loading('正在删除');
    if (!row) return true;
    try {
      await deleteGeneratorUsingPost({
        id: row.id as any,
      });
      hide();
      message.success('删除成功');
      actionRef?.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('删除失败，' + error.message);
      return false;
    }
  };

  const highlight = useEmotionCss(() => {
    return {
      backgroundColor: 'yellow'
    }
  })

  const columns: ProColumns<API.Generator>[] = [
    {
      title: '关键字',
      dataIndex: 'searchText',
      hideInTable: true,
      renderFormItem: () => {
        return <Input placeholder="在名称作者和描述中搜索" />;
      },
      hideInForm: true
    },
    {
      title: 'id',
      dataIndex: 'id',
      valueType: 'text',
      align: 'center',
      hideInForm: true,
      hideInTable: true,
    },
    {
      title: '图片',
      dataIndex: 'picture',
      valueType: 'text',
      align: 'center',
      fieldProps: {
        width: 64,
      },
      hideInSearch: true,
      hideInForm: true,
      render: (url) => {
        return <Image width="100px" src={MINIO_HOST + url} />;
      }
    },
    {
      title: '名称',
      dataIndex: 'name',
      align: 'center',
      valueType: 'text',
      hideInSearch: true,
      render: (text) => {
        const keyword = formRef.current?.getFieldValue('searchText');
        if (keyword && typeof text === 'string' && text.toLowerCase().includes(keyword.toLowerCase())) {
          const parts = text.split(new RegExp(`(${keyword})`, 'gi'));
          return <span>{parts.map((part: string) => part.toLowerCase() === keyword.toLowerCase() ?
            <span key={part} className={highlight}>{part}</span> : part)}</span>;
        }
        return text;
      },
    },
    {
      title: '作者',
      dataIndex: 'author',
      align: 'center',
      valueType: 'text',
      hideInForm: true,
      hideInSearch: true,
      render: (text) => {
        const keyword = formRef.current?.getFieldValue('searchText');
        if (keyword && typeof text === 'string' && text.toLowerCase().includes(keyword.toLowerCase())) {
          const parts = text.split(new RegExp(`(${keyword})`, 'gi'));
          return <span>{parts.map((part: string) => part.toLowerCase() === keyword.toLowerCase() ?
            <span key={part} className={highlight}>{part}</span> : part)}</span>;
        }
        return text;
      }
    },
    {
      title: '标签',
      dataIndex: 'tags',
      align: 'center',
      valueType: 'text',
      renderFormItem: () => {
        const options = tags.map((tag) => ({
          value: tag,
          label: <Tag color={"cyan"}>{tag}</Tag>,
        }));
        return <Select placeholder={"请选择"} options={options} mode="tags" />;
      },
      render: (_, record) => {
        if (!record.tags) {
          return <></>
        }
        return JSON.parse(record.tags).map(((tag: string) => {
          return (<Tag key={tag} color={"cyan"}>{tag}</Tag>)
        }))
      }
    },
    {
      title: '描述',
      dataIndex: 'description',
      align: 'center',
      valueType: 'text',
      hideInSearch: true,
      render: (text) => {
        const keyword = formRef.current?.getFieldValue('searchText');
        if (keyword && typeof text === 'string' && text.toLowerCase().includes(keyword.toLowerCase())) {
          const parts = text.split(new RegExp(`(${keyword})`, 'gi'));
          return <span>{parts.map((part: string) => part.toLowerCase() === keyword.toLowerCase() ?
            <span key={part} className={highlight}>{part}</span> : part)}</span>;
        }
        return text;
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      align: 'center',
      valueType: 'text',
      hideInForm: true
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      align: 'center',
      valueType: 'dateTime',
      sorter: true,
      hideInForm: true
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      align: 'center',
      valueType: 'dateTime',
      sorter: true,
      hideInForm: true
    },
    {
      title: '基础包名',
      dataIndex: 'basePackage',
      valueType: 'text',
      hideInTable: true
    },
    {
      title: '版本',
      dataIndex: 'version',
      valueType: 'text',
      hideInTable: true
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      align: 'center',
      render: (_, record) => (
        <Space size="middle">
          <Link to={`/generator/update?id=${record.id}`}>
            <Typography.Link>
              修改
            </Typography.Link>
          </Link>
          <Typography.Link type="danger" onClick={() => handleDelete(record)}>
            删除
          </Typography.Link>
        </Space>
      ),
    }
  ]

  return (
    <div>
      <ProTable<API.Generator, API.GeneratorQueryRequest>
        toolBarRender={() => [
          <Link key="link" to="/generator/add">
            <Button
              type="primary"
              key="primary"
            >
              <PlusOutlined /> 新建
            </Button>
          </Link>
        ]}
        actionRef={actionRef}
        formRef={formRef}
        search={{
          span: 6
        }}
        headerTitle={'制作器列表'}
        rowKey="id"
        request={async (params, sort, filter) => {
          const sortField = Object.keys(sort)?.[0]
          const sortOrder = sort?.[sortField] ?? undefined
          const { data, code } = await listGeneratorByPageUsingPost({
            ...params,
            sortField,
            sortOrder,
            ...filter,
          } as API.GeneratorQueryRequest);

          const allTags = new Set();
          if (data && data.records) {
            data.records.forEach((record) => {
              JSON.parse(record.tags as string).forEach((tag: string) => {
                allTags.add(tag);
              });
            });
            setTags(Array.from(allTags) as string[]);
          }
          return {
            success: code === 0,
            data: data?.records || [],
            total: Number(data?.total) || 0,
          };
        }}
        columns={columns}
      />
    </div>
  )
}

export default Generator
