import { useColorModeValue } from '@/components/ui/color-mode';
import {
  DialogBackdrop,
  DialogBody,
  DialogCloseTrigger,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogRoot,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { UserCredentials } from '@/types';
import {
  closeLoginModalAtom,
  isLoginModalOpenAtom,
  openRegisterModalAtom,
} from '@/utils/atoms/modal-atoms';
import { useUser } from '@/utils/providers/user-provider/use-user';
import {
  FormControl,
  FormErrorMessage,
  FormLabel,
} from '@chakra-ui/form-control';
import { Button, Input } from '@chakra-ui/react';
import { useAtom } from 'jotai';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';

const LoginDialog = () => {
  const [isLoginModalOpen] = useAtom(isLoginModalOpenAtom);
  const [, closeLoginModal] = useAtom(closeLoginModalAtom);
  const [, openRegisterModal] = useAtom(openRegisterModalAtom);
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useUser();

  const {
    handleSubmit,
    register,
    formState: { errors },
  } = useForm<UserCredentials>();

  function onSubmit(values: UserCredentials) {
    setIsLoading(true);
    login(values)
      .then(() => {
        closeLoginModal();
        setIsLoading(false);
      })
      .catch((error) => {
        console.error(error);
        closeLoginModal();
        setIsLoading(false);
        toast.error(
          'Failed to login, please check your credentials and try again',
        );
      });
  }

  function switchModals() {
    closeLoginModal();
    setTimeout(() => {
      openRegisterModal();
    }, 100);
  }

  const currentTheme = useColorModeValue('light', 'dark');

  return (
    <DialogRoot
      placement="center"
      size="lg"
      scrollBehavior="inside"
      open={isLoginModalOpen}
      onInteractOutside={closeLoginModal}
      onEscapeKeyDown={closeLoginModal}
      motionPreset="slide-in-bottom"
    >
      <DialogBackdrop
        bg="rgba(0, 0, 0, 0.4)"
        _dark={{ bg: 'rgba(0, 0, 0, 0.7 )' }}
        css={{ backdropFilter: 'blur(2px)' }}
      />
      <DialogTrigger />
      <DialogContent
        shadow="xl"
        p="4"
        css={{
          position: 'fixed',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
        }}
      >
        <DialogCloseTrigger onClick={closeLoginModal} _icon={{ w: 6, h: 6 }} />
        <DialogHeader>
          <DialogTitle fontSize="xl">Sign In</DialogTitle>
        </DialogHeader>
        <DialogBody data-theme={currentTheme}>
          <form onSubmit={handleSubmit(onSubmit)}>
            <FormControl isInvalid={!!errors.email}>
              <FormLabel htmlFor="email">E-mail</FormLabel>
              <Input
                id="email"
                _focus={{ outline: 'none' }}
                {...register('email', {
                  required: 'E-mail is required',
                  pattern: {
                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i,
                    message: 'Invalid e-mail address',
                  },
                })}
              />
              <FormErrorMessage
                mt={6}
                color="#cc0000"
                _dark={{ color: '#ff8080' }}
              >
                {errors.email?.message as string}
              </FormErrorMessage>
            </FormControl>
            <FormControl isInvalid={!!errors.password}>
              <FormLabel htmlFor="password" mt={16}>
                Password
              </FormLabel>
              <Input
                id="password"
                type="password"
                _focus={{ outline: 'none' }}
                {...register('password', {
                  required: 'Password is required',
                  pattern: {
                    value: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/,
                    message:
                      'Minimum eight characters, at least one letter and one number',
                  },
                })}
              />
              <FormErrorMessage
                mt={6}
                color="#cc0000"
                _dark={{ color: '#ff8080' }}
              >
                {errors.password?.message as string}
              </FormErrorMessage>
            </FormControl>
            <Button
              fontSize="lg"
              bg="primary"
              color="textBg"
              _hover={{ bg: 'hover' }}
              disabled={isLoading}
              type="submit"
              mt={8}
            >
              Submit
            </Button>
          </form>
        </DialogBody>
        <DialogFooter fontSize="md">
          Don't have an account yet?{' '}
          <Button
            variant="outline"
            color="textRaw"
            onClick={switchModals}
            disabled={isLoading}
          >
            Sign Up
          </Button>
        </DialogFooter>
      </DialogContent>
    </DialogRoot>
  );
};

export default LoginDialog;
