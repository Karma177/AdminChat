package trollnetwork.karma177.adminchat.commands;

import trollnetwork.karma177.adminchat.ChatManager;
import trollnetwork.karma177.adminchat.utils.Messages;
import trollnetwork.karma177.adminchat.utils.PermissionChecker;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import java.util.List;
import java.util.Map;

public class StaffListCommand implements SimpleCommand {

    private final ChatManager chatManager;

    public StaffListCommand(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    private Component toComponent(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    @Override
    public void execute(Invocation invocation) {
        if(!PermissionChecker.hasStaffChatPermission(invocation)) {
            invocation.source().sendMessage(toComponent(Messages.get("no_permission")));
            return;
        }

        Map<String, List<String>> groupedStaff = chatManager.getStaffGroupedByServer();

        if (groupedStaff.isEmpty()) {
            invocation.source().sendMessage(toComponent(Messages.get("stafflist.none")));
        } else {
            // Conta totale
            int total = groupedStaff.values().stream().mapToInt(List::size).sum();
            String header = Messages.get("stafflist.header").replace("{count}", String.valueOf(total));
            invocation.source().sendMessage(toComponent(header));

            String format = Messages.get("stafflist.entry_server");
            
            // Ordina i server alfabeticamente
            groupedStaff.keySet().stream().sorted().forEach(server -> {
                String names = String.join(", ", groupedStaff.get(server));
                invocation.source().sendMessage(toComponent(format
                    .replace("{server}", server)
                    .replace("{players}", names)
                    .replace("{count}", String.valueOf(groupedStaff.get(server).size()))));
            });
        }
    }
}
