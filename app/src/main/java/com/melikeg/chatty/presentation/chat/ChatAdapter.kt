import com.melikeg.chatty.databinding.SentMessageBinding
import com.melikeg.chatty.domain.model.Message
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melikeg.chatty.databinding.ReceivedMessageBinding

class ChatAdapter(private val currentUserId: String, private val messageList: ArrayList<Message>) :
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
            val viewHolder = holder as SentViewHolder
            viewHolder.sentMessage.text = currentMessage.messageText

        }
        else{
            val currentMessage = messageList[position]
            val viewHolder = holder as ReceivedViewHolder
            viewHolder.receivedMessage.text = currentMessage.messageText
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

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)

        val currentMessage = messageList[position]

        if(currentMessage.senderId == currentUserId){
            return VIEW_TYPE_MESSAGE_SENT
        }
        else{
            return VIEW_TYPE_MESSAGE_RECEIVED
        }
    }
}