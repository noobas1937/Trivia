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
import com.ecnu.trivia.web.question.domain.QuestionType;
import com.ecnu.trivia.web.question.domain.vo.QuestionVO;
import com.ecnu.trivia.web.question.mapper.QuestionMapper;
import com.ecnu.trivia.web.question.mapper.QuestionTypeMapper;
import com.ecnu.trivia.web.room.mapper.RoomMapper;
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
    private QuestionTypeMapper questionTypeMapper;
    @Resource
    private PlayerMapper playerMapper;
    @Resource
    private GameMapper gameMapper;
    @Resource
    private MessageService messageService;
    @Resource
    private RoomMapper roomMapper;

    /**
     * 为系统增加问题
     * @author Lucto
     * @date 19:51 2017/12/17
     */
    public void addQuestion(String content,String chooseA,String chooseB,String chooseC,String chooseD,Integer answer,Integer type){
        questionMapper.addQuestion(content,chooseA,chooseB,chooseC,chooseD,answer,type);
    }

    /**
     * 通过问题id获取游戏
     * @author Lucto
     * @date 21:20 2017/12/17
     */
    public List<Game> getGameByQuestionId(Integer questionId) {
        return gameMapper.getGameByQuestionId(questionId);
    }

    /**
     * 删除问题
     * @author Lucto
     * @date 21:39 2017/12/17
     */
    public void deleteQuestion(Integer questionId) {
        questionMapper.deleteQuestion(questionId);
    }

    /**
     * 编辑问题
     * @author Lucto
     * @date 22:32 2017/12/17
     */
    public Resp modifyQuestion(Question questionParam) {
        Question question = questionMapper.getQuestionById(questionParam.getId());
        if(ObjectUtils.isNullOrEmpty(question)){
            return new Resp(HttpRespCode.QUESTION_DOES_NOT_EXISTS);
        }
        questionParam.transform(question);
        questionMapper.modifyQuestion(questionParam);
        return new Resp(HttpRespCode.SUCCESS);
    }

    /**
     * 根据问题ID获取问题题干和选项
     * @param userId
     * @param questionId
     * @author Jack Chen
     */
    public Resp getQuestionById(Integer userId, Integer questionId){
        //判断玩家是否合法 && 游戏是否为待回答问题状态
        Player player = playerMapper.getPlayerByUserId(userId);
        if(player == null) { return new Resp(HttpRespCode.METHOD_NOT_ALLOWED); }
        Game game = gameMapper.getGameByRoomId(player.getRoomId());
        if(!game.getStage().equals(Constants.GAME_ANSWERING_QUESTION)){
            return new Resp(HttpRespCode.METHOD_NOT_ALLOWED);
        }
        //判断当前玩家是不是该玩家
        Integer curPlayerID = game.getCurrentPlayerId();
        if(curPlayerID == null || !Objects.equals(curPlayerID, player.getId())) {
            return new Resp(HttpRespCode.METHOD_NOT_ALLOWED);
        }
        Question question = questionMapper.getQuestionById(questionId);
        if(ObjectUtils.isNullOrEmpty(question)){
            logger.error(ConstantsMsg.GET_QUESTION_ERROR,questionId);
            return new Resp(HttpRespCode.INTERNAL_SERVER_ERROR);
        }
        question.setAnswer(null);
        return new Resp(HttpRespCode.SUCCESS,question);
    }

    /**
     * 用户回答问题逻辑
     *  1、当玩家答题正确，添加一枚金币
     *  2、当玩家回答错误，将玩家的状态设置为HOLD
     *  发送一次UI包
     *  检查游戏是否结束，若已结束，组织游戏结果发送到客户端
     *                  若未结束，组织下一个玩家操作的UI包发送到客户端
     * @param userId
     * @param userAnswer
     * @return
     */
    public Resp checkQuestionAnswer(Integer userId, Integer userAnswer){
        //判断玩家是否合法 && 游戏是否为待回答问题状态
        Player player = playerMapper.getPlayerByUserId(userId);
        if(player == null) { return new Resp(HttpRespCode.METHOD_NOT_ALLOWED); }
        Game game = gameMapper.getGameByRoomId(player.getRoomId());
        if(!game.getStage().equals(Constants.GAME_ANSWERING_QUESTION)){
            return new Resp(HttpRespCode.METHOD_NOT_ALLOWED);
        }
        //判断当前玩家是不是该玩家
        Integer curPlayerID = game.getCurrentPlayerId();
        if(curPlayerID == null || !Objects.equals(curPlayerID, player.getId())) {
            return new Resp(HttpRespCode.METHOD_NOT_ALLOWED);
        }
        //校验问题答案
        Question question = questionMapper.getQuestionByUserId(userId);
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
        gameMapper.updateGameStatus(game.getId(),curPlayerID,game.getDiceNumber(),game.getQuestionId(),Constants.GAME_ANSWER_QUESTION_RESULT);
        messageService.refreshUI(game.getRoomId());

        //检查游戏是否结束（即玩家是否达到6枚金币）
        Player newPlayer = playerMapper.getPlayerByUserId(userId);
        List<Player> players = playerMapper.getPlayers(newPlayer.getId());
        if(newPlayer.getBalance()>=Constants.MAX_BALANCE_COUNT){
                //游戏结束
                logger.info(ConstantsMsg.ROOM_GAME_OVER,game.getRoomId());
                //更新玩家状态 和 游戏状态
                gameMapper.updateGameStatus(game.getId(),-1,0,-1,Constants.GAME_OVER);
            roomMapper.updateRoomStatus(game.getRoomId(),Constants.ROOM_WAITING);
            //刷新一波游戏结果
            messageService.refreshUI(player.getRoomId());
            //清空玩家数据
            for (Player p : players) {
                playerMapper.updatePlayer(p.getId(), 0, 0, Constants.PLAYER_WAITING);
            }
        }else{
            //游戏未结束，转向下一个玩家
            Integer nextPlayer = null;
            for (int i = 0; i < players.size(); i++) {
                if(Objects.equals(players.get(i).getId(), newPlayer.getId())){
                    nextPlayer = ++i%players.size();
                    nextPlayer = players.get(nextPlayer).getId();
                    break;
                }
            }
            if(nextPlayer==null){
                logger.error(ConstantsMsg.ROOM_STATE_ERROR);
            }else{
                gameMapper.updateGameStatus(game.getId(),nextPlayer,-1,-1,Constants.GAME_READY);
            }
        }
        //刷新UI
        messageService.refreshUI(player.getRoomId());
        return new Resp(HttpRespCode.SUCCESS,answerResult);
    }

    /**
     * 获取所有问题
     * @author Lucto
     * @date 23:03 2017/12/17
     */
    public List<QuestionVO> getAllQuestions(Integer pno, Integer PAGE_SIZE) {
        Integer npno = (pno - 1) * PAGE_SIZE;
        return questionMapper.getQuestions(npno,PAGE_SIZE);
    }

    public Integer getAllQuestionsCount() { return questionMapper.getQuestionsCount(); }

    /**
     * 根据问题类型随机生成问题
     *  1、判断是否为该玩家操作阶段
     *  2、判断问题类型是否存在
     *  3、随机生成问题，更新数据库，刷新数据包
     * @param userId
     * @param type
     * @author Jack Chen
     */
    public Resp generateRandomQuestion(Integer userId, Integer type) {
        //判断玩家是否合法 && 游戏是否为选择问题类型状态
        Player player = playerMapper.getPlayerByUserId(userId);
        if (player == null) {
            return new Resp(HttpRespCode.METHOD_NOT_ALLOWED);
        }
        Game game = gameMapper.getGameByRoomId(player.getRoomId());
        if (!game.getStage().equals(Constants.GAME_CHOOSE_TYPE)) {
            return new Resp(HttpRespCode.METHOD_NOT_ALLOWED);
        }
        //判断当前玩家是不是该玩家
        Integer curPlayerID = game.getCurrentPlayerId();
        if (curPlayerID == null || !Objects.equals(curPlayerID, player.getId())) {
            return new Resp(HttpRespCode.METHOD_NOT_ALLOWED);
        }
        //2、校验问题类型是否合法
        QuestionType questionType = questionTypeMapper.getQuestionTypeById(type);
        if(ObjectUtils.isNullOrEmpty(questionType)){
            return new Resp(HttpRespCode.PARAM_ERROR);
        }
        //3.1、生成相应类型的随机题目
        Integer questionId = questionMapper.generateRandomQuestion(type);
        if (ObjectUtils.isNullOrEmpty(questionId)) {
            logger.error(ConstantsMsg.QUESTION_CHOOSE_ERROR, userId, type);
            return new Resp(HttpRespCode.INTERNAL_SERVER_ERROR);
        }
        //3.2、更新数据库
        gameMapper.updateGameStatus(game.getId(), player.getId(), game.getDiceNumber(), questionId, Constants.GAME_ANSWERING_QUESTION);
        //3.3、刷新数据包
        messageService.refreshUI(player.getRoomId());
        return new Resp(HttpRespCode.SUCCESS);
    }
}
