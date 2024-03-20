import FileUploader from '@/components/FileUploader';
import PictureUploader from '@/components/PictureUploader';
import {
  ProCard,
  ProFormInstance,
  ProFormItem,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  StepsForm,
} from '@ant-design/pro-components';
import {useEffect, useRef, useState} from 'react';
import {message, UploadFile} from "antd";
import {
  addGeneratorUsingPost,
  getGeneratorVoByIdUsingGet,
  updateGeneratorUsingPost
} from "@/services/backend/generatorController";
import {history} from "@umijs/max";
import {useSearchParams} from "react-router-dom";
import {MINIO_HOST} from "@/constants";

const GeneratorAdd = () => {
  const formRef = useRef<ProFormInstance>();
  const [searchParams] = useSearchParams()
  const id = searchParams.get('id')
  const [oldData, setOldData] = useState<API.GeneratorUpdateRequest>()
  const loadDate = async () => {
    if (!id) {
      return
    }
    try {
      const res = await getGeneratorVoByIdUsingGet({id: id as any})
      if (res.data) {
        const {distPath} = res.data
        if (distPath) {
          // @ts-ignore
          res.data.distPath = [
            {
              uid: id,
              name: '文件' + id,
              status: 'done',
              url: MINIO_HOST + distPath,
              response: distPath
            } as UploadFile
          ]
        }
        setOldData(res.data)
      }
    } catch (e: any) {
      message.error('加载数据失败' + e.message)
    }
  }

  useEffect(() => {
    if (id) {
      loadDate()
    }
  }, [id])

  const doAdd = async (values: API.GeneratorAddRequest) => {
    try {
      const res = await addGeneratorUsingPost(values)
      if (res.data) {
        message.success('创建成功')
        history.push(`/generator/detail/${res.data}`)
      }
    } catch (e: any) {
      message.error('创建失败' + e.message)
    }
  }

  const doUpdate = async (values: API.GeneratorUpdateRequest) => {
    try {
      const res = await updateGeneratorUsingPost(values)
      if (res.data) {
        message.success('修改成功')
        history.push(`/generator/detail/${id}`)
      }
    } catch (e: any) {
      message.error('修改失败' + e.message)
    }
  }

  const doSubmit = async (values: API.GeneratorAddRequest) => {
    if (!values.fileConfig) {
      values.fileConfig = {}
    }
    if (!values.modelConfig) {
      values.modelConfig = {}
    }
    if (values.distPath && values.distPath.length > 0) {
      // @ts-ignore
      values.distPath = values.distPath[0].response
    }
    if (id) {
      await doUpdate({id: id as any, ...values})
    } else {
      await doAdd(values)
    }
  }

  return (
    <ProCard>
      {(!id || oldData) && (
        <StepsForm<API.GeneratorAddRequest> formRef={formRef} onFinish={doSubmit} formProps={{initialValues: oldData}}>
          <StepsForm.StepForm
            name="base"
            title="基本信息"
            onFinish={async () => {
              console.log(formRef.current?.getFieldsValue());
              return true;
            }}
          >
            <ProFormText name="name" label="名称" placeholder="请输入名称"/>
            <ProFormTextArea name="description" label="描述" placeholder="请输入描述"/>
            <ProFormText name="basePackage" label="基础包" placeholder="请输入基础包"/>
            <ProFormText name="version" label="版本" placeholder="请输入版本"/>
            <ProFormSelect name="tags" label="标签" mode="tags" placeholder="请输入标签列表"/>
            <ProFormItem label="封面" name="picture">
              <PictureUploader biz="generator_picture"/>
            </ProFormItem>
          </StepsForm.StepForm>
          <StepsForm.StepForm name="fileConfig" title="文件配置">
            todo
          </StepsForm.StepForm>
          <StepsForm.StepForm name="modelConfig" title="模型配置">
            todo
          </StepsForm.StepForm>
          <StepsForm.StepForm name="dist" title="生成器文件">
            <ProFormItem label="产物包" name="distPath">
              <FileUploader biz="generator_dist" description="请上传生成器文件压缩包"/>
            </ProFormItem>
          </StepsForm.StepForm>
        </StepsForm>
      )}
    </ProCard>
  );
};

export default GeneratorAdd;
