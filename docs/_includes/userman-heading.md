{% assign num = include.section | append: "." %}
{% assign head-tag = "#" %}
{% if include.sub %}
  {% assign num = num | append: include.sub | append: "." %}
  {% assign head-tag = head-tag | append: "#" %}
{% endif %}
{% if include.subsub %}
  {% assign num = num | append: include.subsub | append: "." %}
  {% assign head-tag = head-tag | append: "#" %}
{% endif %}
<a name="{{include.title | downcase | replace: " ", "_" | replace: "/", "_"}}"/>
{{head-tag}} {{num}} {{include.title}}
