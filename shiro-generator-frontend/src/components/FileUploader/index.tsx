import { uploadFileUsingPost } from '@/services/backend/fileController';
import { InboxOutlined } from '@ant-design/icons';
import {message, UploadFile, UploadProps} from 'antd';
import Dragger from 'antd/es/upload/Dragger';
import { UploadRequestOption } from 'rc-upload/lib/interface';
import React, { useState } from 'react';

interface Props {
  biz: string;
  description?: string;
  value?: UploadFile[];
  onChange?: (fileList: UploadFile[]) => void;
}

const FileUploader: React.FC<Props> = (props) => {
  const { biz, description, value, onChange } = props;
  const [loading, setLoading] = useState<boolean>(false);
  const uploadProps: UploadProps = {
    name: 'file',
    listType: 'text',
    multiple: false,
    maxCount: 1,
    fileList: value,
    disabled: loading,
    onChange: ({ fileList }) => {
      onChange?.(fileList);
    },
    customRequest: async (fileObj: UploadRequestOption) => {
      setLoading(true);
      try {
        const res = await uploadFileUsingPost({ biz }, {}, fileObj.file as File);
        fileObj.onSuccess?.(res.data);
      } catch (e: any) {
        message.error('上传失败, ' + e.message);
        fileObj.onError?.(e);
      }
      setLoading(false);
    },
  };

  return (
    <Dragger {...uploadProps}>
      <p className="ant-upload-drag-icon">
        <InboxOutlined onPointerEnterCapture={undefined} onPointerLeaveCapture={undefined} />
      </p>
      <p className="ant-upload-text">点击或拖拽上传文件</p>
      <p className="ant-upload-hint">{description}</p>
    </Dragger>
  );
};

export default FileUploader;
