import React from "react";
import {Avatar, Card} from "antd";

interface Props {
  user: API.UserVO
}

const AuthorInfo: React.FC<Props> = (props) => {

  const {user} = props
  if (!user) {
    return <>无信息</>
  }
  return (
    <div style={{marginTop: 16}}>
      <Card.Meta avatar={<Avatar src={user.userAvatar} size={64}/>} title={user.userName}
                 description={user.userProfile}/>
    </div>
  )
}

export default AuthorInfo
