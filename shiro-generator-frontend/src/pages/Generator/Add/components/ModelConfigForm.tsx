import { Button, Card, Form, FormListFieldData, Input, Radio, Select, Space } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import React, { useState } from 'react';
import { ProFormInstance } from '@ant-design/pro-components';

interface Props {
  formRef: React.MutableRefObject<ProFormInstance | undefined>;
  oldData: any;
}

interface SingleFieldFormProps {
  field: FormListFieldData;
  remove?: (index: number | number[]) => void;
}

const SingleFieldForm: React.FC<SingleFieldFormProps> = ({ field, remove }) => {
  const [type, setType] = useState<string>();
  return (
    <Space key={field.key}>
      <Form.Item label="字段名称" name={[field.name, 'fieldName']}>
        <Input />
      </Form.Item>
      <Form.Item label="描述" name={[field.name, 'description']}>
        <Input />
      </Form.Item>
      <Form.Item label="类型" name={[field.name, 'type']}>
        <Select
          style={{ width: 100 }}
          options={[
            { value: 'String', label: '字符串型' },
            { value: 'Boolean', label: '布尔型' },
          ]}
          onChange={(value) => {
            setType(value);
          }}
        />
      </Form.Item>
      {type &&
        (type === 'String' ? (
          <Form.Item label="默认值" name={[field.name, 'defaultValue']}>
            <Input />
          </Form.Item>
        ) : (
          <Form.Item label="默认值" name={[field.name, 'defaultValue']}>
            <Radio.Group>
              <Radio value={true}>是</Radio>
              <Radio value={false}>否</Radio>
            </Radio.Group>
          </Form.Item>
        ))}
      <Form.Item label="缩写" name={[field.name, 'abbr']}>
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

const ModelConfigForm: React.FC<Props> = (props) => {
  const { formRef, oldData } = props;

  return (
    <Form.List name={['modelConfig', 'models']}>
      {(fields, { add, remove }) => (
        <div style={{ display: 'flex', rowGap: 16, flexDirection: 'column' }}>
          {fields.map((field) => {
            const modelConfig =
              formRef.current?.getFieldsValue().modelConfig ?? oldData.modelConfig;
            const groupKey = modelConfig.models?.[field.name]?.groupKey;
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
                    <Form.Item label="描述" name={[field.name, 'description']}>
                      <Input />
                    </Form.Item>
                    <Form.Item label="分组类别" name={[field.name, 'type']}>
                      <Input />
                    </Form.Item>
                    <Form.Item label="条件" name={[field.name, 'condition']}>
                      <Input />
                    </Form.Item>
                  </Space>
                ) : (
                  <SingleFieldForm field={field} />
                )}
                {groupKey && (
                  <Form.Item>
                    <Form.List name={[field.name, 'models']}>
                      {(subFields, subOpt) => (
                        <div style={{ display: 'flex', rowGap: 16, flexDirection: 'column' }}>
                          {subFields.map((subField) => (
                            <SingleFieldForm
                              key={subField.key}
                              field={subField}
                              remove={subOpt.remove}
                            />
                          ))}
                          <Button type="dashed" onClick={() => subOpt.add()} block>
                            添加组内字段
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
            添加字段
          </Button>
          <Button
            type="primary"
            onClick={() => add({ groupName: '分组', groupKey: 'group' })}
            block
          >
            添加分组
          </Button>
          <div style={{ marginBottom: 16 }} />
        </div>
      )}
    </Form.List>
  );
};

export default ModelConfigForm;
