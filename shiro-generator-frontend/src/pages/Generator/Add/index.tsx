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
import { useRef } from 'react';

const GeneratorAdd = () => {
  const formRef = useRef<ProFormInstance>();

  return (
    <ProCard>
      <StepsForm<API.GeneratorAddRequest> formRef={formRef}>
        <StepsForm.StepForm
          name="base"
          title="基本信息"
          onFinish={async () => {
            console.log(formRef.current?.getFieldsValue());
            return true;
          }}
        >
          <ProFormText name="name" label="名称" placeholder="请输入名称" />
          <ProFormTextArea name="description" label="描述" placeholder="请输入描述" />
          <ProFormText name="basePackage" label="基础包" placeholder="请输入基础包" />
          <ProFormText name="version" label="版本" placeholder="请输入版本" />
          <ProFormSelect name="tags" label="标签" mode="tags" placeholder="请输入标签列表" />
          <ProFormItem label="封面" name="picture">
            <PictureUploader biz="generator_picture" />
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
            <FileUploader biz="generator_dist" description="请上传生成器文件压缩包" />
          </ProFormItem>
        </StepsForm.StepForm>
      </StepsForm>
    </ProCard>
  );
};

export default GeneratorAdd;
