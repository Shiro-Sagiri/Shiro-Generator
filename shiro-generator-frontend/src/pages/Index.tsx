import {listGeneratorVoByPageUsingPost} from "@/services/backend/generatorController";
import {Avatar, Card, Flex, Input, List, message, Tag, Typography} from "antd";
import React, {useEffect, useState} from "react";
import {ProFormSelect, QueryFilter} from "@ant-design/pro-form";
import {PageContainer, ProFormText} from "@ant-design/pro-components";
import moment from "moment";
import {UserOutlined} from "@ant-design/icons";

const IndexPage = () => {

  const DEFAULT_PAGE_PARAMS: PageRequest = {
    current: 1,
    pageSize: 4,
    sortField: 'createTime',
    sortOrder: 'ascend'
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

  const tagListView = (tagList: string[]) => {
    if (!tagList) {
      return <></>
    }
    return <div>
      {tagList.map((tag) => (
        <Tag key={tag}>{tag}</Tag>
      ))}
    </div>
  }

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
              value={searchParams.searchText}
              onChange={(e) => {
                searchParams.searchText = e.target.value
              }}
              onSearch={(value: string) => {
                setSearchParams({...DEFAULT_PAGE_PARAMS, searchText: value})
              }}
            />
          </Flex>
        </div>

        <QueryFilter
          submitter={false}
          span={8}
          labelWidth="auto"
          onFinish={async (value: API.GeneratorQueryRequest) => {
            setSearchParams({
              ...DEFAULT_PAGE_PARAMS,
              ...value
            })
          }}
        >
          <ProFormText label='名称' name="name"/>
          <ProFormText label='描述' name="description"/>
          <ProFormSelect label='标签' name="tags" mode={"tags"}/>
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
            <Card
              hoverable
              cover={<img src={item.picture} alt={item.name}/>}
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
          </List.Item>
        )
        }
      />
    </PageContainer>
  )
}

export default IndexPage
