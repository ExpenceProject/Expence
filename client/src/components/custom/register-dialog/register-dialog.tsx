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
import { UserRegisterData } from '@/types';
import {
  closeRegisterModalAtom,
  isRegisterModalOpenAtom,
  openLoginModalAtom,
} from '@/utils/atoms/modal-atoms';
import { useUser } from '@/utils/providers/user-provider/use-user';
import {
  FormControl,
  FormErrorMessage,
  FormLabel,
} from '@chakra-ui/form-control';
import { Button, Input, Text } from '@chakra-ui/react';
import { useAtom } from 'jotai';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';

const RegisterDialog = () => {
  const [isRegisterModalOpen] = useAtom(isRegisterModalOpenAtom);
  const [, closeRegisterModal] = useAtom(closeRegisterModalAtom);
  const [, openLoginModal] = useAtom(openLoginModalAtom);
  const { registerUser } = useUser();

  const {
    handleSubmit,
    register,
    formState: { errors, isSubmitting },
  } = useForm<UserRegisterData>();

  function onSubmit(values: UserRegisterData) {
    registerUser(values)
      .then(() => {
        closeRegisterModal();
        toast.success('Account created successfully');
      })
      .catch((error) => {
        console.error(error);
        closeRegisterModal();
        toast.error(
          'Failed to create account, please try again in a few minutes',
        );
      });
  }

  function switchModals() {
    closeRegisterModal();
    setTimeout(() => {
      openLoginModal();
    }, 100);
  }

  const currentTheme = useColorModeValue('light', 'dark');

  return (
    <DialogRoot
      placement="center"
      size="lg"
      scrollBehavior="inside"
      open={isRegisterModalOpen}
      onInteractOutside={closeRegisterModal}
      onEscapeKeyDown={closeRegisterModal}
      motionPreset="slide-in-bottom"
    >
      <DialogBackdrop
        bg="rgba(0, 0, 0, 0.4)"
        _dark={{ bg: 'rgba(0, 0, 0, 0.7 )' }}
        css={{ backdropFilter: 'blur(2px)' }}
      />
      <DialogTrigger />
      <DialogContent shadow="xl" p="4">
        <DialogCloseTrigger
          _icon={{ w: 6, h: 6 }}
          onClick={closeRegisterModal}
        />
        <DialogHeader>
          <DialogTitle fontSize="xl">Sign Up</DialogTitle>
        </DialogHeader>
        <DialogBody data-theme={currentTheme}>
          <form onSubmit={handleSubmit(onSubmit)}>
            <FormControl isInvalid={!!errors.firstName}>
              <FormLabel htmlFor="firstName">First name*</FormLabel>
              <Input
                id="firstName"
                type="text"
                _focus={{ outline: 'none' }}
                {...register('firstName', {
                  required: 'First name is required',
                  minLength: {
                    value: 2,
                    message: 'Minimum 2 characters',
                  },
                })}
              />
              <FormErrorMessage
                mt={6}
                color="#cc0000"
                _dark={{ color: '#ff8080' }}
              >
                {errors.firstName?.message as string}
              </FormErrorMessage>
            </FormControl>
            <FormControl isInvalid={!!errors.lastName}>
              <FormLabel htmlFor="lastName" mt={16}>
                Last name*
              </FormLabel>
              <Input
                id="lastName"
                type="text"
                _focus={{ outline: 'none' }}
                {...register('lastName', {
                  required: 'Last name is required',
                  minLength: {
                    value: 2,
                    message: 'Minimum 2 characters',
                  },
                })}
              />
              <FormErrorMessage
                mt={6}
                color="#cc0000"
                _dark={{ color: '#ff8080' }}
              >
                {errors.lastName?.message as string}
              </FormErrorMessage>
            </FormControl>
            <FormControl isInvalid={!!errors.phoneNumber}>
              <FormLabel htmlFor="phoneNumber" mt={16}>
                Phone number
              </FormLabel>
              <Input
                id="phoneNumber"
                type="tel"
                css={{
                  '-moz-appearance': 'textfield',
                  '&::-webkit-inner-spin-button': { display: 'none' },
                  '&::-webkit-outer-spin-button': { display: 'none' },
                }}
                _focus={{ outline: 'none' }}
                {...register('phoneNumber', {
                  pattern: {
                    value: /^[0-9]{9}$/,
                    message: 'Invalid phone number, must be 9 digits',
                  },
                })}
              />
              <FormErrorMessage
                mt={6}
                color="#cc0000"
                _dark={{ color: '#ff8080' }}
              >
                {errors.phoneNumber?.message as string}
              </FormErrorMessage>
            </FormControl>
            <FormControl isInvalid={!!errors.password}></FormControl>
            <FormControl isInvalid={!!errors.email}>
              <FormLabel htmlFor="email" mt={16}>
                E-mail*
              </FormLabel>
              <Input
                id="email"
                autoComplete="new-password"
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
                Password*
              </FormLabel>
              <Input
                id="password"
                type="password"
                _focus={{ outline: 'none' }}
                autoComplete="new-password"
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
            <Text pt={3} color="disabled">
              * &nbsp;—&nbsp; Required fields
            </Text>
            <Button
              fontSize="lg"
              bg="primary"
              color="textBg"
              _hover={{ bg: 'hover' }}
              disabled={isSubmitting}
              type="submit"
              mt={8}
            >
              Submit
            </Button>
          </form>
        </DialogBody>
        <DialogFooter fontSize="md">
          Already have an account?{' '}
          <Button variant="outline" color="textRaw" onClick={switchModals}>
            Sign In
          </Button>
        </DialogFooter>
      </DialogContent>
    </DialogRoot>
  );
};

export default RegisterDialog;
