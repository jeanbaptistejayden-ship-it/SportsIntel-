from django.urls import path
from . import views

urlpatterns = [
    path('search/', views.player_search, name='player_search'),
    path('summary/', views.player_summary, name='player_summary'),
    path('gamelog/', views.player_gamelog, name='player_gamelog'),
    path('compare/', views.player_compare, name='player_compare'),
    path('vs-opponent/', views.player_vs_opponent, name='player_vs_opponent'),
]
