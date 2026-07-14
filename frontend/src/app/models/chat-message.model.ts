export interface ChatMessage {
  role: 'user' | 'bot';
  content: string;
  timestamp: Date;
}
