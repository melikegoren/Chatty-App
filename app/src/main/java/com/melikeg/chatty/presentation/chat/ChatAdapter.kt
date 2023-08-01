import com.melikeg.chatty.databinding.SentMessageBinding
import com.melikeg.chatty.domain.model.Message
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melikeg.chatty.databinding.ReceivedMessageBinding
import com.melikeg.chatty.domain.model.ChatbotMessage

class ChatAdapter<T : Any>(private val currentUserId: String, private val messageList: ArrayList<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == VIEW_TYPE_MESSAGE_RECEIVED){
            val inflater = LayoutInflater.from(parent.context)
            val binding = ReceivedMessageBinding.inflate(inflater, parent, false)
            return ReceivedViewHolder(binding)
        }
        else{
            val inflater = LayoutInflater.from(parent.context)
            val binding = SentMessageBinding.inflate(inflater, parent, false)
            return SentViewHolder(binding)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.javaClass == SentViewHolder::class.java){
            val currentMessage = messageList[position]

            if(currentMessage.javaClass == Message::class.java){
                currentMessage as Message
                val viewHolder = holder as ChatAdapter<*>.SentViewHolder
                viewHolder.sentMessage.text = currentMessage.messageText
            }
            else if(currentMessage.javaClass == ChatbotMessage::class.java){
                currentMessage as ChatbotMessage
                val viewHolder = holder as ChatAdapter<*>.SentViewHolder
                viewHolder.sentMessage.text = currentMessage.messageText
            }


        }
        else{
            val currentMessage = messageList[position]
            if(currentMessage.javaClass == Message::class.java){
                currentMessage as Message
                val viewHolder = holder as ChatAdapter<*>.ReceivedViewHolder
                viewHolder.receivedMessage.text = currentMessage.messageText
            }

            else if(currentMessage.javaClass == ChatbotMessage::class.java){
                currentMessage as ChatbotMessage
                val viewHolder = holder as ChatAdapter<*>.ReceivedViewHolder
                viewHolder.receivedMessage.text = currentMessage.messageText
            }

        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }


    inner class SentViewHolder(binding: SentMessageBinding): RecyclerView.ViewHolder(binding.root){
        val sentMessage = binding.tvMessage
    }

    inner class ReceivedViewHolder(binding: ReceivedMessageBinding): RecyclerView.ViewHolder(binding.root){
        val receivedMessage = binding.tvMessage

    }

    fun addMessage(message: T) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)

        val currentMessage = messageList[position]

        if(currentMessage.javaClass == Message::class.java){
            currentMessage as Message
            if(currentMessage.senderId == currentUserId){
                return VIEW_TYPE_MESSAGE_SENT
            }
            else{
                return VIEW_TYPE_MESSAGE_RECEIVED
            }
        }
        else{
            currentMessage as ChatbotMessage
            if(currentMessage.sender == currentUserId){
                return VIEW_TYPE_MESSAGE_SENT
            }
            else{
                return VIEW_TYPE_MESSAGE_RECEIVED
            }
        }
    }
}