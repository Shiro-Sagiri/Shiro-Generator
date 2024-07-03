import { Alert, Button, Card, Divider, Form, FormListFieldData, Input, Select, Space } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import React from 'react';
import { ProFormInstance } from '@ant-design/pro-components';

interface Props {
  formRef: React.MutableRefObject<ProFormInstance | undefined>;
  oldData: any;
}

const FileConfigForm: React.FC<Props> = (props) => {
  const { formRef, oldData } = props;

  const SingleFieldForm = (
    field: FormListFieldData,
    remove?: (index: number | number[]) => void,
  ) => {
    return (
      <Space key={field.key}>
        <Form.Item label="输入路径" name={[field.name, 'inputPath']}>
          <Input />
        </Form.Item>
        <Form.Item label="输出路径" name={[field.name, 'outputPath']}>
          <Input />
        </Form.Item>
        <Form.Item label="类型" name={[field.name, 'type']}>
          <Select
            defaultValue="file"
            options={[
              { value: 'file', label: '文件' },
              { value: 'dir', label: '目录' },
            ]}
          />
        </Form.Item>
        <Form.Item label="生成类型" name={[field.name, 'generateType']}>
          <Select
            defaultValue="dynamic"
            options={[
              { value: 'static', label: '静态' },
              { value: 'dynamic', label: '动态' },
            ]}
          />
        </Form.Item>
        <Form.Item label="条件" name={[field.name, 'condition']}>
          <Input />
        </Form.Item>
        {remove && (
          <Button type="text" danger onClick={() => remove(field.name)}>
            删除
          </Button>
        )}
      </Space>
    );
  };

  return (
    <>
      <Alert message="如果不需要在线制作功能,可不填写此表单" type="warning" closable />
      <Divider />
      <Form.List name={['fileConfig', 'files']}>
        {(fields, { add, remove }) => (
          <div style={{ display: 'flex', rowGap: 16, flexDirection: 'column' }}>
            {fields.map((field) => {
              const fileConfig =
                formRef.current?.getFieldsValue().fileConfig ?? oldData?.fileConfig;
              const groupKey = fileConfig.files?.[field.name]?.groupKey;
              return (
                <Card
                  style={{ border: '1px dashed' }}
                  size={'small'}
                  title={groupKey ? '分组' : '未分组字段'}
                  key={field.key}
                  extra={<CloseOutlined onClick={() => remove(field.name)} />}
                >
                  {groupKey ? (
                    <Space>
                      <Form.Item label="分组Key" name={[field.name, 'groupKey']}>
                        <Input />
                      </Form.Item>
                      <Form.Item label="组名" name={[field.name, 'groupName']}>
                        <Input />
                      </Form.Item>
                      <Form.Item label="条件" name={[field.name, 'condition']}>
                        <Input />
                      </Form.Item>
                    </Space>
                  ) : (
                    SingleFieldForm(field)
                  )}
                  {groupKey && (
                    <Form.Item>
                      <Form.List name={[field.name, 'files']}>
                        {(subFields, subOpt) => (
                          <div style={{ display: 'flex', rowGap: 16, flexDirection: 'column' }}>
                            {subFields.map((subField) => SingleFieldForm(subField, subOpt.remove))}
                            <Button type="dashed" onClick={() => subOpt.add()} block>
                              添加组内文件
                            </Button>
                          </div>
                        )}
                      </Form.List>
                    </Form.Item>
                  )}
                </Card>
              );
            })}
            <Button type="primary" onClick={() => add()}>
              添加文件
            </Button>
            <Button
              type="primary"
              onClick={() => add({ groupName: '分组', groupKey: 'group', type: 'group' })}
              block
            >
              添加分组
            </Button>
            <div style={{ marginBottom: 16 }} />
          </div>
        )}
      </Form.List>
    </>
  );
};

export default FileConfigForm;
