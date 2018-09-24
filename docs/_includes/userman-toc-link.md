{% assign num = include.section | append: "." %}
{% assign head-level = 1 %}
{% if include.sub %}
  {% assign num = num | append: include.sub | append: "." %}
  {% assign head-level = head-level | plus: 1 %}
{% endif %}
{% if include.subsub %}
  {% assign num = num | append: include.subsub | append: "." %}
  {% assign head-level = head-level | plus: 1 %}
{% endif %}
{% assign url = include.section-title | downcase | replace: " ", "_" | replace: "/", "_" | append: ".html" %}
{% if include.title %}
  {% assign anchor = include.title | downcase | replace: " ", "_" | replace: "/", "_" | replace: "🔹", "" %}
  {% assign url = url | append: "#" | append: anchor %}
  {% assign title = include.title %}
{% else %}
  {% assign title = include.section-title %}
{% endif %}
{% assign indent = head-level | minus: 1 | times: 4 %}
{% assign style = "text-indent: " | append: indent | append: "em;" %}
{% if head-level == 1 %}
  {% assign style = style | append: " font-size: 1.6em" %}
{% endif %}
<h{{head-level}} style="{{style}}"><a href="{{site.baseurl}}/user_manual/{{url}}">{{num}} {{title}}</a></h{{head-level}}>
