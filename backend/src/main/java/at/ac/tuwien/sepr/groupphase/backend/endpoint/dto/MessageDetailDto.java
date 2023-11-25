package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Dto to send message detail data to the frontend.
 */
public class MessageDetailDto extends SimpleMessageDto {

  private String text;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MessageDetailDto that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return Objects.equals(text, that.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), text);
  }

  @Override
  public String toString() {
    return "DetailedMessageDto{"
        + "text='" + text + '\''
        + '}';
  }

  /**
   *  DetailedMessageDtoBuilder. 
   */
  public static final class DetailedMessageDtoBuilder {
    private Long id;
    private LocalDateTime publishedAt;
    private String text;
    private String title;
    private String summary;

    private DetailedMessageDtoBuilder() {
    }

    public static DetailedMessageDtoBuilder detailedMessageDto() {
      return new DetailedMessageDtoBuilder();
    }

    public DetailedMessageDtoBuilder withId(Long id) {
      this.id = id;
      return this;
    }

    public DetailedMessageDtoBuilder withPublishedAt(LocalDateTime publishedAt) {
      this.publishedAt = publishedAt;
      return this;
    }

    public DetailedMessageDtoBuilder withText(String text) {
      this.text = text;
      return this;
    }

    public DetailedMessageDtoBuilder withTitle(String title) {
      this.title = title;
      return this;
    }

    public DetailedMessageDtoBuilder withSummary(String summary) {
      this.summary = summary;
      return this;
    }

    /**
    * Message detail build.
    *
    * @return builded message detail dto
    */
    public MessageDetailDto build() {
      MessageDetailDto messageDetailDto = new MessageDetailDto();
      messageDetailDto.setId(id);
      messageDetailDto.setPublishedAt(publishedAt);
      messageDetailDto.setText(text);
      messageDetailDto.setTitle(title);
      messageDetailDto.setSummary(summary);
      return messageDetailDto;
    }
  }
}