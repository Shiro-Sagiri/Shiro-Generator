import { ProForm } from '@ant-design/pro-form';
import { ProFormItem } from '@ant-design/pro-components';
import FileUploader from '@/components/FileUploader';
import { Collapse, message } from 'antd';
import { makeGeneratorUsingPost } from '@/services/backend/generatorController';
import { saveAs } from 'file-saver';

interface Props {
  meta: API.GeneratorAddRequest | API.GeneratorUpdateRequest;
}

const GeneratorMaker = (props: Props) => {
  const { meta } = props;
  const [form] = ProForm.useForm();

  const doSubmit = async (values: API.GeneratorMakeRequest) => {
    if (!meta.name) {
      message.error('请填写名称');
      return;
    }
    const zipFilePath = values.zipFilePath;
    if (!zipFilePath || zipFilePath.length < 1) {
      message.error('请上传模板文件压缩包');
      return;
    }
    //@ts-ignore
    values.zipFilePath = zipFilePath[0].response;
    try {
      const blob = await makeGeneratorUsingPost(
        {
          meta,
          zipFilePath: values.zipFilePath,
        },
        {
          responseType: 'blob',
        },
      );
      saveAs(blob, meta.name + '.zip');
      message.success('下载成功!');
    } catch (error: any) {
      message.error('下载失败' + error.message);
    }
  };

  const formView = (
    <ProForm
      form={form}
      submitter={{
        searchConfig: {
          submitText: '制作',
        },
        resetButtonProps: {
          hidden: true,
        },
      }}
      onFinish={doSubmit}
    >
      <ProFormItem label="模板文件" name="zipFilePath">
        <FileUploader
          biz="generator_make_template"
          description="请上传压缩包,打包时不要添加最外层目录!"
        />
      </ProFormItem>
    </ProForm>
  );

  return (
    <Collapse
      style={{
        marginBottom: 24,
      }}
      items={[
        {
          key: 'maker',
          label: '生成器制作工具',
          children: formView,
        },
      ]}
    />
  );
};

export default GeneratorMaker;
