import {GithubOutlined} from '@ant-design/icons';
import {DefaultFooter} from '@ant-design/pro-components';
import '@umijs/max';
import React from 'react';

const Footer: React.FC = () => {
  const defaultMessage = '空白';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'github',
          title: (
            <>
              <GithubOutlined/> Shiro
            </>
          ),
          href: 'https://github.com/Shiro-Sagiri',
          blankTarget: true,
        },
        {
          key: 'ICP',
          title: (
            <>
              赣ICP备2024023022号-1
            </>
          ),
          href: 'https://beian.miit.gov.cn/',
          blankTarget: true
        }
      ]}
    />
  );
};
export default Footer;
