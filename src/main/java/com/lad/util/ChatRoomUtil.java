package com.lad.util;

import com.lad.bo.UserBo;
import com.lad.service.IUserService;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashSet;

/**
 * Created by unisky on 2017/11/10.
 */
public class ChatRoomUtil {

    // 生成群聊名称
    public static String generateChatRoomName(IUserService userService,
                                               LinkedHashSet<String> userIds,
                                               String chatRoomId,
                                               Logger logger){
        StringBuilder builder = new StringBuilder();
        for(String userId: userIds){
            UserBo userBo = userService.getUser(userId);
            if(userBo != null){
                String userName = userBo.getUserName();
                builder.append(userName);
                builder.append('、');
            }else{
                if(logger != null)
                logger.error(String.format("%s id的用户不存在，但在群聊(id:%s)中存在", userId, chatRoomId));
            }
        }

        if(builder.length() > 0){
            builder.deleteCharAt(builder.length()-1);
            return builder.toString();
        }

        if(logger != null)
        logger.error(String.format("群聊（id：%s）中没有一个有效用户", chatRoomId));
        return null;

    }

    /**
     *  生成 userName0,userName1,userName2 id0,id1,id2的形式
     *
     *  为IMUtil.notifyInChatRoom处使用
     * @param userIds
     * @return
     */
    public static String getUserNamesAndIds(IUserService userService, String[] userIds, Logger logger){
        StringBuilder nameBuilder = new StringBuilder();
        StringBuilder idBuilder = new StringBuilder();
        for(String userId: userIds){
            UserBo userBo = userService.getUser(userId);
            if(userBo == null){
                if(logger != null){
                    logger.error(String.format("userId:%s is not exists"));
                }

            }else{
                nameBuilder.append(userBo.getUserName());
                nameBuilder.append(",");

                idBuilder.append(userId);
                idBuilder.append(",");
            }
        }

        if(idBuilder.length() == 0) return null;

        idBuilder.deleteCharAt(idBuilder.length()-1);
        nameBuilder.deleteCharAt(nameBuilder.length()-1);

        return nameBuilder.toString() + " " + idBuilder.toString();

    }

}
