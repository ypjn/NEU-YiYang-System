package neu.YYZX.controller;

import neu.YYZX.model.Message;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController extends BaseController {

    @GetMapping
    public Map<String, Object> list(@RequestParam String receiverName) {
        List<Message> all = ctx.getMessageDao().findAll();
        List<Message> result = new ArrayList<>();
        for (Message m : all) {
            if (receiverName.equals(m.getReceiverName())) {
                result.add(m);
            }
        }
        Collections.reverse(result); // 最新在前
        return success("ok", result);
    }

    @GetMapping("/unread-count")
    public Map<String, Object> unreadCount(@RequestParam String receiverName) {
        List<Message> all = ctx.getMessageDao().findAll();
        long count = all.stream()
                .filter(m -> receiverName.equals(m.getReceiverName()) && !m.isRead())
                .count();
        return success("ok", count);
    }

    @PutMapping("/{id}/read")
    public Map<String, Object> markRead(@PathVariable String id) {
        Message m = ctx.getMessageDao().findById(id);
        if (m == null) return error("消息不存在");
        m.setRead(true);
        ctx.getMessageDao().update(m);
        saveId();
        return success("已读");
    }
}
