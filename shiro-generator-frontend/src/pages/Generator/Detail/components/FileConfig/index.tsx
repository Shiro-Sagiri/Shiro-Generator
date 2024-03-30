import React from "react";
import {Descriptions, DescriptionsProps, Divider} from "antd";
import {FileOutlined} from "@ant-design/icons";

interface Props {
  fileConfig: API.FileConfig
}

const FileConfig: React.FC<Props> = (props) => {
  const {fileConfig} = props
  if (!fileConfig) {
    return <></>
  }

  // const baseInfoItems: DescriptionsProps['items'] = [
  //   {
  //     key: 'inputRootPath',
  //     label: '输入根路径',
  //     children: <p>{fileConfig.inputRootPath}</p>
  //   },
  //   {
  //     key: 'outputRootPath',
  //     label: '输出根路径',
  //     children: <p>{fileConfig.outputRootPath}</p>
  //   },
  //   {
  //     key: 'sourceRootPath',
  //     label: '项目根路径',
  //     children: <p>{fileConfig.sourceRootPath}</p>
  //   },
  //   {
  //     key: 'type',
  //     label: '文件类型',
  //     children: <p>{fileConfig.type}</p>
  //   }
  // ]

  const fileListView = (files?: API.FileInfo[]) => {
    if (!files) {
      return <>无信息</>
    }
    return (
      <>
        {files.map((file, index) => {
          if (file.groupKey) {
            const groupFileItems: DescriptionsProps['items'] = [
              {
                key: 'groupKey',
                label: '分组key',
                children: <p>{file.groupKey}</p>
              },
              {
                key: 'groupName',
                label: '分组名称',
                children: <p>{file.groupName}</p>
              },
              {
                key: 'condition',
                label: '条件',
                children: <p>{file.condition}</p>
              },
              {
                key: 'files',
                label: '组内文件',
                children: <p>{fileListView(file.files)}</p>
              }
            ]
            return <Descriptions key={index} column={1} title={file.groupName} items={groupFileItems}/>
          }

          const fileItems: DescriptionsProps['items'] = [
            {
              key: 'inputPath',
              label: '输入路径',
              children: <p>{file.inputPath}</p>
            },
            {
              key: 'outputPath',
              label: '输出路径',
              children: <p>{file.outputPath}</p>
            },
            {
              key: 'generatorType',
              label: '文件生成类型',
              children: <p>{file.generateType}</p>
            },
            {
              key: 'type',
              label: '文件类型',
              children: <p>{file.type}</p>
            },
            {
              key: 'condition',
              label: '条件',
              children: <p>{file.condition}</p>
            }
          ]

          return <>
            <Descriptions column={2} key={index} items={fileItems}/>
            <Divider/>
          </>
        })}
      </>
    )
  }

  return (
    <div>
      {/*<Descriptions title={<><InfoCircleOutlined/> 基本信息</>} column={2} items={baseInfoItems}/>*/}
      <div style={{marginBottom: 16}}></div>
      <Descriptions title={<><FileOutlined/> 文件列表</>}/>
      {fileListView(fileConfig.files)}
    </div>
  )
}

export default FileConfig
