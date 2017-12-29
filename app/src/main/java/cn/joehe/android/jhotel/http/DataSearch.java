package cn.joehe.android.jhotel.http;

import java.security.Timestamp;
import java.util.List;

/**
 * Created by hemiao on 2017/12/25.
 */

public class DataSearch {
    private int hotelSearchCount;
    private List<Hotel> hotels;



    public int getHotelSearchCount() {
        return hotelSearchCount;
    }

    public String getHdsSeq() {
        return hotels.get(0).getRooms().get(0).getHdsSeq();
    }

    public String getRoomId() {
        return hotels.get(0).getRooms().get(0).getRoomId();
    }

    public double getTotalPrice() {
        return hotels.get(0).getRooms().get(0).getRatePlans().get(0).getTotalPrice().getAmount();
    }

    public String getDate() {
        return hotels.get(0).getRooms().get(0).getRatePlans().get(0).getDailyRates().get(0).getDate();
    }

    public double getPrice() {
        return hotels.get(0).getRooms().get(0).getRatePlans().get(0).getDailyRates().get(0).getPrice().getAmount();
    }

//    public double getAmount() {
//        return hotels.get(0).getRooms().get(0).getRatePlans().get(0).getDailyRates().get(0).getPrice().getAmount();
//    }

    private class Hotel {
        private String hdsSeq;
        private String hotelSeq;
        private String hotelName;
        private String hotelAddress;
        private String gradeName;
        private String starName;
        private String hotelArea;
        private String bPoint;
        private String gPoint;
        private int scale;
        private double minPrice;
        private double internetMinPrice;
        private double commentScore;
        private List<Room> rooms;

        public String getHdsSeq() {
            return hdsSeq;
        }

        public String getHotelSeq() {
            return hotelSeq;
        }

        public String getHotelName() {
            return hotelName;
        }

        public String getHotelAddress() {
            return hotelAddress;
        }

        public String getGradeName() {
            return gradeName;
        }

        public String getStarName() {
            return starName;
        }

        public String getHotelArea() {
            return hotelArea;
        }

        public String getbPoint() {
            return bPoint;
        }

        public String getgPoint() {
            return gPoint;
        }

        public int getScale() {
            return scale;
        }

        public double getMinPrice() {
            return minPrice;
        }

        public double getInternetMinPrice() {
            return internetMinPrice;
        }

        public double getCommentScore() {
            return commentScore;
        }

        public List<Room> getRooms() {
            return rooms;
        }

        private class Room {
            private String roomId;
            private String roomName;
            private String bedType;
            private String bedDesc;
            private String wrapperId;
            private String hdsSeq;
            private String hotelId;
            private String bath;
            private String maxCustomers;
            private String area;
            private String broadBand;
            private String floor;
            private String wifi;
            private String window;
            private List<RatePlan> ratePlans;

            public String getRoomId() {
                return roomId;
            }

            public String getRoomName() {
                return roomName;
            }

            public String getBedType() {
                return bedType;
            }

            public String getBedDesc() {
                return bedDesc;
            }

            public String getWrapperId() {
                return wrapperId;
            }

            public String getHdsSeq() {
                return hdsSeq;
            }

            public String getHotelId() {
                return hotelId;
            }

            public String getBath() {
                return bath;
            }

            public String getMaxCustomers() {
                return maxCustomers;
            }

            public String getArea() {
                return area;
            }

            public String getBroadBand() {
                return broadBand;
            }

            public String getFloor() {
                return floor;
            }

            public String getWifi() {
                return wifi;
            }

            public String getWindow() {
                return window;
            }

            public List<RatePlan> getRatePlans() {
                return ratePlans;
            }

            private class RatePlan {
                private int supplierId;
                private int hdsRatePlanId;
                private String planName;
                private String refundRuleStr;
                private String cancelRuleType;
                private List<DailyRate> dailyRates;
                private Price totalPrice;
                private String payType;
                private boolean instantConfirmation;
                private boolean forceGuarantee;
                private boolean allowSale;
                private boolean pack;

                public int getSupplierId() {
                    return supplierId;
                }

                public int getHdsRatePlanId() {
                    return hdsRatePlanId;
                }

                public String getPlanName() {
                    return planName;
                }

                public String getRefundRuleStr() {
                    return refundRuleStr;
                }

                public String getCancelRuleType() {
                    return cancelRuleType;
                }

                public List<DailyRate> getDailyRates() {
                    return dailyRates;
                }

                public Price getTotalPrice() {
                    return totalPrice;
                }

                public String getPayType() {
                    return payType;
                }

                public boolean isInstantConfirmation() {
                    return instantConfirmation;
                }

                public boolean isForceGuarantee() {
                    return forceGuarantee;
                }

                public boolean isAllowSale() {
                    return allowSale;
                }

                public boolean isPack() {
                    return pack;
                }

                private class DailyRate {
                    private String date;
                    private Price price;
                    private String hasBreakfast;
                    private int availableRooms;
                    private String roomState;
                    private boolean allowOverBooking;
                    private boolean instantConfirmation;

                    public String getDate() {
                        return date;
                    }

                    public Price getPrice() {
                        return price;
                    }

                    public String getHasBreakfast() {
                        return hasBreakfast;
                    }

                    public int getAvailableRooms() {
                        return availableRooms;
                    }

                    public String getRoomState() {
                        return roomState;
                    }

                    public boolean isAllowOverBooking() {
                        return allowOverBooking;
                    }

                    public boolean isInstantConfirmation() {
                        return instantConfirmation;
                    }
                }

                private class Price {
                    private double amount;
                    private String currency;

                    public double getAmount() {
                        return amount;
                    }

                    public String getCurrency() {
                        return currency;
                    }
                }
            }
        }
    }
}
