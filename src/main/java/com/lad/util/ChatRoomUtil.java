package com.lad.util;

import com.lad.bo.UserBo;
import com.lad.service.IUserService;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
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
     *  为IMUtil.notifyInChatRoom处使用
     * @param userIds
     * @return
     */
    public static Object[] getUserNamesAndIds(IUserService userService, String[] userIds, Logger logger){

        Object[] objects = new Object[2];
        ArrayList<String> ids = new ArrayList<>(userIds.length);
        ArrayList<String> names = new ArrayList<>(userIds.length);

        for(String userId: userIds){
            UserBo userBo = userService.getUser(userId);
            if(userBo == null){
                if(logger != null){
                    logger.error(String.format("userId:%s is not exists"));
                }

            }else{
                names.add(userBo.getUserName());
                ids.add(userId);
            }
        }

        if(ids.size() == 0) return objects;

        objects[0] = names;
        objects[1] = ids;
        return objects;

    }

}
