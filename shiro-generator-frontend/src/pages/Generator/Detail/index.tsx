import {useParams} from "react-router";
import {downloadGeneratorByIdUsingGet, getGeneratorVoByIdUsingGet} from "@/services/backend/generatorController";
import {Button, Card, Col, Image, message, Row, Space, Tabs, Typography} from "antd";
import React, {useEffect, useState} from "react";
import {tagListView} from "@/pages";
import moment from "moment";
import {DownloadOutlined, EditOutlined} from "@ant-design/icons";
import {PageContainer} from "@ant-design/pro-components";
import FileConfig from "@/pages/Generator/Detail/components/FileConfig";
import ModelConfig from "@/pages/Generator/Detail/components/ModelConfig";
import AuthorInfo from "@/pages/Generator/Detail/components/AuthorInfo";
import {saveAs} from "file-saver";
import {Link} from "umi";
import {useModel} from "@umijs/max";
import {MINIO_HOST} from "@/constants";

const GeneratorDetail: React.FC = () => {
  const {id} = useParams()
  const [data, setData] = useState<API.GeneratorVO>({})
  const [loading, setLoading] = useState<boolean>(false)
  const {initialState} = useModel('@@initialState')

  const {currentUser} = initialState ?? {}

  const loadDate = async () => {
    if (!id) {
      return
    }
    setLoading(true)
    try {
      const res = await getGeneratorVoByIdUsingGet({id: id as any})
      if (res.data) {
        setData(res.data)
      }
    } catch (e: any) {
      message.error('加载数据失败' + e.message)
    }
    setLoading(false)
  }

  useEffect(() => {
    loadDate()
  }, [id])

  const DownloadButton = data.distPath && currentUser && (
    <Button
      icon={<DownloadOutlined/>}
      onClick={async () => {
        const blob = await downloadGeneratorByIdUsingGet({id: data.id}, {responseType: 'blob'})
        const fullPath = data.distPath || ''
        saveAs(blob, fullPath.substring(fullPath.lastIndexOf('/') + 1))
        message.success('下载中...')
      }}
    >下载</Button>
  )

  const EditButton = (currentUser?.id === data.user?.id) && (
    <Link to={`/generator/update?id=${data.id}`}>
      <Button icon={<EditOutlined/>}>编辑</Button>
    </Link>
  )

  return (
    <PageContainer>
      <Card loading={loading}>
        <Row justify="space-between" gutter={[32, 32]}>
          <Col flex="auto">
            <Space size="large" align="center">
              <Typography.Title level={4}>{data.name}</Typography.Title>
              {tagListView(data.tags ?? [])}
            </Space>
            <Typography.Paragraph>{data.description}</Typography.Paragraph>
            <Typography.Paragraph
              type="secondary">创建时间: {moment(data.createTime).format('YYYY-MM-DD hh:mm:ss')}</Typography.Paragraph>
            <Typography.Paragraph type="secondary">基础包: {data.basePackage}</Typography.Paragraph>
            <Typography.Paragraph type="secondary">版本: {data.version}</Typography.Paragraph>
            <Typography.Paragraph type="secondary">作者: {data.author}</Typography.Paragraph>
            <div style={{marginBottom: 24}}/>
            <Space size="middle">
              <Button type="primary">立即使用</Button>
              {DownloadButton}
              {EditButton}
            </Space>
          </Col>
          <Col flex="320px">
            <Image src={MINIO_HOST + data.picture}/>
          </Col>
        </Row>
      </Card>
      <div style={{marginBottom: 24}}/>
      <Card>
        <Tabs
          size="large"
          defaultActiveKey={'fileConfig'}
          onChange={() => {
          }}
          items={[
            {
              key: 'fileConfig',
              label: '文件配置',
              children: <FileConfig fileConfig={data.fileConfig ?? {}}/>
            },
            {
              key: 'modelConfig',
              label: '模型配置',
              children: <ModelConfig modelConfig={data.modelConfig ?? {}}/>
            },
            {
              key: 'userInfo',
              label: '作者信息',
              children: <AuthorInfo user={data.user ?? {}}/>
            },
          ]}
        />
      </Card>
    </PageContainer>
  )
}

export default GeneratorDetail
