package NettyServerCourseWork.handlerService;

import NettyServerCourseWork.repository.GameRepository;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class HelpHandlerService extends BaseHandlerService {

    private final GameRepository gameRepository;

    public HelpHandlerService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Map<String, String> command = decryptByteBuff((ByteBuf)msg);
        if (command.get("options") != null) {
            String info = "\n---\nСписок игр с кратким описанием:\n" +
                    "1) Кости - gamename: dice - классические кости для 2+ человек\n" +
                    "2) Рулетка - gamename: roulette - рулетка для 2+ человек\n---\n";

            ctx.channel().writeAndFlush(info);
        } else {
            String greetings = "\n---\nРаспределенное казино находится в альфа доступе, со временем команды могут полностью " +
                    "изменяться, или менять свою сигнатуру.\n---\n" +
                    "Для взаимодействия с казино необходимо хранить свой токен и держать его в секрете. " +
                    "В случае утечки вашего токена в открытый доступ, просим немедленно его заменить соответствующей командой.\n---\n" +
                    "Стоит отметить, решение с токенам, на данный момент, временно и связанно " +
                    "с ограничениями локальной разработки учебного проекта, решить эту проблему поможет закупка " +
                    "выделенного сервера и выделенного белого IP для сервера. Спасибо за понимание.\n---\n";
            String message = greetings + "Основные команды: \n>>\tlogin <username> <password> - вход в систему\n"+
                    ">>\tregistration <username> <password> - регистрация в системе\n" +
                    ">>\thelp game - список игр\n" +
                    ">>\tgame <gamename> <token> [sum] <bet> - подключение к игре <gamename> со ставкой [sum] и выбором <bet>\n" +
                    ">>\tbalance <token> - узнать свой баланс\n" +
                    ">>\tpay <token> <sum> - пополнить свой баланс на <sum> рублей\n" +
                    ">>\tchat <token> <to> <message> - отправить сообщение <message> игроку с ником <to>\n" +
                    ">>\thelp\n---\n";
            ctx.channel().writeAndFlush(message);
        }
    }

    @Override
    String[] getCommandTrigger() {
        return new String[]{"help"};
    }

    @Override
    Map<String, String> decryptByteBuff(ByteBuf byteBuf) {
        String[] rawData = byteBuf.toString(Charset.defaultCharset()).trim().split(" ");

        HashMap<String, String> command = new HashMap<>();
        command.put("command", rawData[0]);
        if (rawData.length > 1) {
            command.put("options", rawData[1]);
        }

        return command;
    }
}
