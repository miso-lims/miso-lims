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
{% assign url = include.section-title | downcase | replace: " ", "_" | replace: "/", "_" | append: ".html" %}
{% if include.title %}
  {% assign anchor = include.title | downcase | replace: " ", "_" | replace: "/", "_" | replace: "🔹", "" %}
  {% assign url = url | append: "#" | append: anchor %}
  {% assign title = include.title %}
{% else %}
  {% assign title = include.section-title %}
{% endif %}
{{head-tag}} [{{num}} {{title}}]({{site.baseurl}}/user_manual/{{url}})
