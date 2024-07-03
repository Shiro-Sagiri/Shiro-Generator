import { PageContainer } from "@ant-design/pro-components";
import { Card, Col, Collapse, Divider, Form, Input, message, Row, Typography, Image, Button, Space, Radio } from "antd";
import { useEffect, useState } from "react";
import { Link, useModel, useParams } from "@@/exports";
import { getGeneratorVoByIdUsingGet, useGeneratorUsingPost } from "@/services/backend/generatorController";
import { MINIO_HOST } from "@/constants";
import { DownloadOutlined } from "@ant-design/icons";
import { saveAs } from "file-saver";

const UseGenerator = () => {
  const { id } = useParams()
  const [loading, setLoading] = useState<boolean>(false);
  const [data, setData] = useState<API.GeneratorVO>();
  const [form] = Form.useForm();
  const models = data?.modelConfig?.models;
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState ?? {}
  const [downloading, setDownloading] = useState<boolean>(false);

  const loadData = async () => {
    if (!id) {
      return;
    }
    setLoading(true);
    try {
      // @ts-ignore
      const res = await getGeneratorVoByIdUsingGet({ id });
      setData(res.data as API.GeneratorVO)
    } catch (error: any) {
      message.error('数据获取失败, ' + error.message)
    }
    setLoading(false)
  }

  useEffect(() => {
    loadData()
  }, [id])

  const download = async () => {
    setDownloading(true)
    const values = form.getFieldsValue();
    try {
      // eslint-disable-next-line react-hooks/rules-of-hooks
      const blob = await useGeneratorUsingPost({
        id: data?.id,
        dataModel: values
      }, { responseType: 'blob' })
      const fullPath = data?.distPath || '';
      saveAs(blob, fullPath.substring(fullPath.lastIndexOf('/') + 1))
    } catch (error: any) {
      message.error("生成器下载失败!")
    } finally {
      setDownloading(false)
    }
  }

  const downloadButton = data?.distPath && currentUser && (
    <Button
      type="primary"
      icon={<DownloadOutlined />}
      loading={downloading}
      onClick={download}
    >
      生成代码
    </Button>
  )

  return (
    <PageContainer title={<></>} loading={loading}>
      <Card>
        <Row justify={"space-between"} gutter={[32, 32]}>
          <Col flex={"auto"}>
            <Typography.Title level={3}>{data?.name}</Typography.Title>
            <Typography.Paragraph>{data?.description}</Typography.Paragraph>
            <Divider />
            <Form form={form}>
              {models?.map((model, index) => {
                //判断是否有分组
                if (model.groupKey) {
                  //分组models为空则直接返回空标签
                  if (!model.models) {
                    return <></>
                  }
                  return (
                    <Collapse
                      bordered={false}
                      key={index}
                      items={[
                        {
                          key: index,
                          label: model.groupName + '(分组)',
                          children: model.models.map((subModel, index) => {
                            return (
                              <Form.Item
                                key={index}
                                label={subModel.fieldName}
                                //@ts-ignore
                                name={[model.groupKey, subModel.fieldName]}
                              >
                                {
                                  subModel.type !== 'Boolean' ?
                                    <Input placeholder={subModel.description} /> :
                                    <Space>
                                      <Typography.Text type="secondary">{subModel.description}</Typography.Text>
                                      <Radio.Group>
                                        <Radio value="true">是</Radio>
                                        <Radio value="false">否</Radio>
                                      </Radio.Group>
                                    </Space>
                                }
                              </Form.Item>
                            )
                          })
                        }
                      ]}
                    />
                  )
                } else {
                  //非分组
                  return (
                    <Form.Item
                      key={index}
                      label={model.fieldName}
                      name={model.fieldName}
                    >
                      {
                        model.type !== 'Boolean' ?
                          <Input placeholder={model.description} /> :

                          <Space>
                            <Typography.Text type="secondary">{model.description}</Typography.Text>
                            <Radio.Group>
                              <Radio value="true">是</Radio>
                              <Radio value="false">否</Radio>
                            </Radio.Group>
                          </Space>
                      }
                    </Form.Item>
                  )
                }
              })}
            </Form>
            <Divider />
            <Space size="middle">
              {downloadButton}
              <Link to={`/generator/detail/${id}`}>
                <Button>查看详细</Button>
              </Link>
            </Space>
          </Col>
          <Col flex="320px">
            <Image src={MINIO_HOST + data?.picture} />
          </Col>
        </Row>
      </Card>
    </PageContainer>
  )
}


export default UseGenerator
