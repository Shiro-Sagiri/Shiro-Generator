import {uploadFileUsingPost} from '@/services/backend/fileController';
import {LoadingOutlined, PlusOutlined} from '@ant-design/icons';
import {message, Upload, UploadProps} from 'antd';
import React, {useState} from 'react';
import {UploadRequestOption} from 'rc-upload/lib/interface';
import {MINIO_HOST} from "@/constants";

interface Props {
  biz: string;
  value?: string;
  onChange?: (url: string) => void;
}

const PictureUploader: React.FC<Props> = (props) => {
  const {biz, value, onChange} = props;
  const [loading, setLoading] = useState(false);

  const uploadProps: UploadProps = {
    name: 'file',
    listType: 'picture-card',
    multiple: false,
    maxCount: 1,
    showUploadList: false,
    customRequest: async (fileObj: UploadRequestOption) => {
      setLoading(true);
      try {
        const res = await uploadFileUsingPost({biz}, {}, fileObj.file as File);
        // 拼接完整图片路径
        onChange?.(res.data as string);
        fileObj.onSuccess?.(res.data);
      } catch (e: any) {
        message.error('上传失败，' + e.message);
        fileObj.onError?.(e);
      }
      setLoading(false);
    },
  };

  const uploadButton = (
    <div>
      {loading ? (
        <LoadingOutlined onPointerEnterCapture={undefined} onPointerLeaveCapture={undefined}/>
      ) : (
        <PlusOutlined onPointerEnterCapture={undefined} onPointerLeaveCapture={undefined}/>
      )}
      <div style={{marginTop: 8}}>上传</div>
    </div>
  );

  return (
    <Upload {...uploadProps}>
      {value ? <img src={MINIO_HOST + value} alt="picture" style={{width: '100%'}}/> : uploadButton}
    </Upload>
  );
};

export default PictureUploader;
