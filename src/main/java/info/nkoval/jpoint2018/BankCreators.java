package info.nkoval.jpoint2018;

public enum BankCreators {
    COARSE_GRAINED {
        @Override
        public Bank create(int n) {
            return new CGBank(n);
        }
    },
/*    FINE_GRAINED {
        @Override
        public Bank create(int n) {
            return new FGBank(n);
        }
    },
    LOCK_FREE {
        @Override
        public Bank create(int n) {
            return new LFBank(n);
        }
    },
    LOCK_FREE_HTM {
        @Override
        public Bank create(int n) {
            return new LFRTMBank(n);
        }
    },*/
    COARSE_GRAINED_HTM {
        @Override
        public Bank create(int n) {
            return new CGRTMBank(n);
        }
    };

    public abstract Bank create(int n);
}
