/**
 * Service编写规范：
 *  1、所有Service类需要添加 @Service 注解标识
 *  2、所有Service类需要实现 Logable 接口
 *  3、所有Service在类内部使用 @Resource 自动注入 Mapper、Service
 *  4、如果有URL或配置常量请使用 @Value("${name}")自动装配
 *  5、所有 Service 类请添加 logger 日志器，并熟练使用logger
 *
 * @author Jack Chen
 */
package com.ecnu.trivia.web.question.service;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.log.Logable;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.web.game.domain.Game;
import com.ecnu.trivia.web.game.domain.Player;
import com.ecnu.trivia.web.game.mapper.GameMapper;
import com.ecnu.trivia.web.game.mapper.PlayerMapper;
import com.ecnu.trivia.web.message.service.MessageService;
import com.ecnu.trivia.web.question.domain.Question;
import com.ecnu.trivia.web.question.mapper.QuestionMapper;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.utils.Constants;
import com.ecnu.trivia.web.utils.ConstantsMsg;
import com.ecnu.trivia.web.utils.Resp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
public class QuestionService implements Logable{

    private static Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private PlayerMapper playerMapper;
    @Resource
    private GameMapper gameMapper;
    @Resource
    private MessageService messageService;

    /**
     * 为系统增加问题
     * @Author: Lucto
     * * @Date: 19:51 2017/12/17
     */
    public void addQuestion(String content,String chooseA,String chooseB,String chooseC,String chooseD,Integer answer,Integer type){
        questionMapper.addQuestion(content,chooseA,chooseB,chooseC,chooseD,answer,type);
    }

    /**
     * 通过问题id获取游戏
     * @Author: Lucto
     * * @Date: 21:20 2017/12/17
     */
    public Game getGameByQuestionId(Integer questionId){
        return gameMapper.getGameByQuestionId(questionId);
    }

    /**
     * 删除问题
     * @Author: Lucto
     * * @Date: 21:39 2017/12/17
     */
    public void deleteQuestion(Integer questionId) {
        questionMapper.deleteQuestion(questionId);
    }

    /**
     * 编辑问题
     * @Author: Lucto
     * * @Date: 22:32 2017/12/17
     */
    public void modifyQuestion(Integer id,String content,String chooseA,String chooseB,String chooseC,String chooseD,Integer answer,Integer type) {
        Question question = questionMapper.getQuestionById(id);
        if(ObjectUtils.isNullOrEmpty(content)){
            content = question.getDescription();
        }
        if(ObjectUtils.isNullOrEmpty(chooseA)){
            chooseA = question.getChooseA();
        }
        if(ObjectUtils.isNullOrEmpty(chooseB)){
            chooseB = question.getChooseB();
        }
        if(ObjectUtils.isNullOrEmpty(chooseC)){
            chooseC = question.getChooseC();
        }
        if(ObjectUtils.isNullOrEmpty(chooseD)){
            chooseD = question.getChooseD();
        }
        if(ObjectUtils.isNullOrEmpty(answer)){
            answer = question.getAnswer();
        }
        if(ObjectUtils.isNullOrEmpty(type)){
            type = question.getTypeId();
        }
        questionMapper.modifyQuestion(id,content,chooseA,chooseB,chooseC,chooseD,answer,type);
    }
    /**
     * 用户回答问题逻辑
     *  1、当玩家答题正确，添加一枚金币
     *  2、当玩家回答错误，将玩家的状态设置为HOLD
     *  发送一次UI包
     *  检查游戏是否结束，若已结束，组织游戏结果发送到客户端
     *                  若未结束，组织下一个玩家操作的UI包发送到客户端
     * @param userId
     * @param questionId
     * @param userAnswer
     * @return
     */
    public Resp checkQuestionAnswer(Integer userId, Integer questionId, Integer userAnswer){
        //判断玩家是否合法 && 游戏是否为待回答问题状态
        Player player = playerMapper.getPlayerByUserId(userId);
        if(player == null) { return new Resp(HttpRespCode.METHOD_NOT_ALLOWED); }
        Game game = gameMapper.getGameById(player.getRoomId());
        if(!game.getStage().equals(Constants.GAME_ANSWERING_QUESTION)){
            return new Resp(HttpRespCode.METHOD_NOT_ALLOWED);
        }
        //判断当前玩家是不是该玩家
        Integer curPlayerID = game.getCurrentPlayerId();
        if(curPlayerID == null || !Objects.equals(curPlayerID, player.getId())) {
            return new Resp(HttpRespCode.METHOD_NOT_ALLOWED);
        }
        //校验问题答案
        Question question = questionMapper.getQuestionById(questionId);
        boolean answerResult = Objects.equals(question.getAnswer(), userAnswer);
        if(answerResult){
            //回答正确 && 加鸡腿
            playerMapper.updatePlayer(curPlayerID,player.getBalance()+1,
                    player.getPosition(),Constants.PLAYER_GAMING_FREE);
        }else{
            //回答错误 && 关禁闭
            playerMapper.updatePlayer(curPlayerID,player.getBalance(),
                    player.getPosition(),Constants.PLAYER_GAMING_HOLD);
        }
        //刷新UI包
        messageService.refreshUI(game.getRoomId());

        //检查游戏是否结束（即玩家是否达到6枚金币）
        Integer curPlayerId = player.getId();
        List<Player> players = playerMapper.getPlayers(curPlayerId);
        if(player.getBalance()+1>=Constants.MAX_BALANCE_COUNT){
            logger.info(ConstantsMsg.ROOM_GAME_OVER,game.getRoomId());
            //更新玩家状态 和 游戏状态
            gameMapper.updateGameStatus(game.getId(),-1,-1,-1,Constants.GAME_OVER);
            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                playerMapper.updatePlayer(p.getId(),p.getBalance(),p.getPosition(),Constants.PLAYER_READY);
            }
        }else{
            //游戏未结束，转向下一个玩家
            Integer nextPlayer = null;
            for (int i = 0; i < players.size(); i++) {
                if(Objects.equals(players.get(i).getId(), curPlayerId)){
                    nextPlayer = ++i%players.size();
                }
            }
            if(nextPlayer==null){
                logger.error(ConstantsMsg.ROOM_STATE_ERROR);
            }else{
                gameMapper.updateGameStatus(game.getId(),nextPlayer,game.getDiceNumber(),game.getQuestionId(),Constants.GAME_READY);
            }
        }
        //刷新UI
        messageService.refreshUI(player.getRoomId());
        return new Resp(HttpRespCode.SUCCESS,answerResult);
    }

    /**
     * 获取所有问题
     * @Author: Lucto
     * * @Date: 23:03 2017/12/17
     */
    public List<Question> getAllQuestions() {
        List<Question> questions = questionMapper.getQuestionList();
        return questions;
    }
}
