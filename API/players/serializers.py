from rest_framework import serializers


class PlayerSummarySerializer(serializers.Serializer):
    name = serializers.CharField()
    team = serializers.CharField()
    position = serializers.CharField()
    height = serializers.CharField()
    weight = serializers.CharField()
    draft_year = serializers.CharField()
    college = serializers.CharField()
    image_url = serializers.CharField(required=False)


class GameLogSerializer(serializers.Serializer):
    date = serializers.CharField()
    opponent = serializers.CharField()
    home = serializers.BooleanField()
    pts = serializers.FloatField()
    reb = serializers.FloatField()
    ast = serializers.FloatField()
    min = serializers.FloatField()
    fg_pct = serializers.FloatField()
    fg3_pct = serializers.FloatField()
    ft_pct = serializers.FloatField()
    stl = serializers.FloatField()
    blk = serializers.FloatField()
    tov = serializers.FloatField()
    plus_minus = serializers.FloatField()


class ComparisonSerializer(serializers.Serializer):
    player_one = serializers.DictField()
    player_two = serializers.DictField()
