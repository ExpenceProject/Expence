import { atom } from 'jotai';

// Login modal
export const isLoginModalOpenAtom = atom(false);

export const openLoginModalAtom = atom(null, (_, set) => {
  set(isLoginModalOpenAtom, true);
});

export const closeLoginModalAtom = atom(null, (_, set) => {
  set(isLoginModalOpenAtom, false);
});

// Register modal
export const isRegisterModalOpenAtom = atom(false);

export const openRegisterModalAtom = atom(null, (_, set) => {
  set(isRegisterModalOpenAtom, true);
});
export const closeRegisterModalAtom = atom(null, (_, set) => {
  set(isRegisterModalOpenAtom, false);
});

// Avatar modal

export const isAvatarModalOpenAtom = atom(false);

export const openAvatarModalAtom = atom(null, (_, set) => {
  set(isAvatarModalOpenAtom, true);
});

export const closeAvatarModalAtom = atom(null, (_, set) => {
  set(isAvatarModalOpenAtom, false);
});
