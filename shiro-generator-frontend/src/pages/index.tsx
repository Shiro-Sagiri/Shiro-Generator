import {listGeneratorVoByPageUsingPost} from "@/services/backend/generatorController";
import React, {useEffect, useState} from "react";
import {ProFormSelect, QueryFilter} from "@ant-design/pro-form";
import {PageContainer, ProFormText} from "@ant-design/pro-components";
import {Avatar, Card, Flex, Input, List, message, Tag, Typography} from "antd";
import { Link } from "@umijs/max";
import moment from "moment";
import { MINIO_HOST } from "@/constants";
import { UserOutlined } from "@ant-design/icons";

export const tagListView = (tagList: string[]) => {
  if (!tagList) {
    return <></>
  }
  return <div>
    {tagList.map((tag) => (
      <Tag key={tag}>{tag}</Tag>
    ))}
  </div>
}

const IndexPage = () => {

  const DEFAULT_PAGE_PARAMS: PageRequest = {
    current: 1,
    pageSize: 5,
    sortField: 'createTime',
    sortOrder: 'descend'
  }

  const [loading, setLoading] = useState<boolean>(false);
  const [dataList, setDataList] = useState<API.GeneratorVO[]>([]);
  const [total, setTotal] = useState<number>(0);
  const [searchParams, setSearchParams] = useState<API.GeneratorQueryRequest>({
    ...DEFAULT_PAGE_PARAMS,
  });

  const doSearch = async () => {
    setLoading(true)
    try {
      const res = await listGeneratorVoByPageUsingPost(searchParams)
      setDataList(res.data?.records ?? [])
      setTotal(res.data?.total ?? 0)
    } catch (error: any) {
      message.error(error.message)
    }
    setLoading(false)
  }

  useEffect(() => {
    doSearch()
  }, [searchParams])

  const tagList = new Set()

  dataList.map((data) => {
    return data.tags?.forEach(item => tagList.add(item))
  });

  const options = Array.from(tagList).map((tag) => ({
    value: tag,
    // @ts-ignore
    label: <Tag color={"cyan"}>{tag}</Tag>,
  }));

  // @ts-ignore
  return (
    <PageContainer pageHeaderRender={() => false}>
      <div
        style={{
          padding: 24,
        }}
      >
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            gap: 8,
          }}
        >
          <Flex justify={"center"}>
            <Input.Search
              style={{
                minWidth: 320,
                width: '40vw'
              }}
              allowClear
              size={"large"}
              placeholder="搜索代码生成器"
              enterButton="搜索"
              onSearch={(value: string) => {
                setSearchParams({...DEFAULT_PAGE_PARAMS, searchText: value})
              }}
            />
          </Flex>
        </div>

        <QueryFilter
          span={8}
          labelWidth="auto"
          defaultCollapsed={false}
          onFinish={async (value: API.GeneratorQueryRequest) => {
            setSearchParams({
              ...DEFAULT_PAGE_PARAMS,
              ...value
            })
          }}
          onReset={() => {
            setSearchParams({
              ...DEFAULT_PAGE_PARAMS
            })
          }}
        >
          <ProFormText label='名称' name="name"/>
          <ProFormSelect label='标签' name="tags" mode={"tags"} options={options}/>
          <ProFormText label='描述' name="description"/>
        </QueryFilter>
      </div>
      <List
        grid={{
          gutter: 16,
          xs: 1,
          sm: 2,
          md: 3,
          lg: 3,
          xl: 4,
          xxl: 4,
        }}
        dataSource={dataList}
        loading={loading}
        rowKey={"id"}
        pagination={{
          position: 'bottom',
          align: 'center',
          current: searchParams.current,
          pageSize: searchParams.pageSize,
          total,
          onChange: (current, pageSize) => {
            setSearchParams({
              ...searchParams,
              current,
              pageSize
            })
          }
        }}
        renderItem={(item) => (
          <List.Item>
            <Link to={`/generator/detail/${item.id}`}>
              <Card
                hoverable
                cover={<img src={MINIO_HOST + item.picture} alt={item.name}/>}
              >
                <Card.Meta
                  title={<a>{item.name}</a>}
                  description={
                    <Typography.Paragraph ellipsis={{rows: 2}}>
                      {item.description}
                    </Typography.Paragraph>
                  }
                />
                {tagListView(item.tags ?? [])}
                <Flex justify={"space-between"} align={"center"}>
                  <Typography.Text type={"secondary"} style={{fontSize: 12}}>
                    {moment(item.createTime).fromNow()}
                  </Typography.Text>
                  <div>
                    <Avatar src={item.user?.userAvatar ?? <UserOutlined/>}/>
                  </div>
                </Flex>
              </Card>
            </Link>
          </List.Item>
        )
        }
      />
    </PageContainer>
  )
}

export default IndexPage
